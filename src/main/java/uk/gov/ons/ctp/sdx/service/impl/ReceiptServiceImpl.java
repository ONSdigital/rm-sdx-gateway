package uk.gov.ons.ctp.sdx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseFeedbackPublisher;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * The service to acknowleged receipts
 */
@Slf4j
@Named
public class ReceiptServiceImpl implements ReceiptService {

  private static final String CASE_REF = "caseRef";
  private static final String RESPONSE_DATE_TIME = "responseDateTime";
  private static final String EXCEPTION_ACKNOWLEGDING_RECEIPT =
          "An unexpected error occured while acknowledging your receipt. ";
  public static final String EXCEPTION_INVALID_RECEIPT = "Invalid receipt. It can't be acknowledged.";

  @Inject
  private CaseFeedbackPublisher caseFeedbackPublisher;

  @Override
  public final void acknowledge(Receipt receipt) throws CTPException {
    log.debug("acknowledging receipt {}", receipt);
    validate(receipt);

    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedback.setCaseRef(receipt.getCaseRef());
    caseFeedback.setResponseDateTime(giveMeCalendarForNow());
    caseFeedback.setInboundChannel(receipt.getInboundChannel());
    caseFeedbackPublisher.send(caseFeedback);
  }

  @Override
  public void acknowledgeFile(InputStream fileContents) throws CTPException {
    log.debug("acknowledgeFile {}", fileContents);
    List<CaseFeedback> caseFeedbacks = parseIt(fileContents);
    caseFeedbacks.forEach(caseFeedback ->  caseFeedbackPublisher.send(caseFeedback));
  }

  /**
   * To validate a receipt
   * @param receipt to be validated
   * @throws CTPException if the receipt does NOT have an inboundChannel.
   */
  private static void validate(Receipt receipt) throws CTPException {
    InboundChannel inboundChannel = receipt.getInboundChannel();
    if (inboundChannel == null) {
      log.error(EXCEPTION_INVALID_RECEIPT);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_INVALID_RECEIPT);
    }
  }

  /**
   * To get a XMLGregorianCalendar for now
   * @return a XMLGregorianCalendar for now
   * @throws CTPException if it can't create a calendar
   */
  public static XMLGregorianCalendar giveMeCalendarForNow() throws CTPException {
    java.util.GregorianCalendar gregorianCalendar = new java.util.GregorianCalendar();
    gregorianCalendar.setTime(new Date());

    javax.xml.datatype.XMLGregorianCalendar result = null;
    try {
      javax.xml.datatype.DatatypeFactory factory = javax.xml.datatype.DatatypeFactory.newInstance();
      result = factory.newXMLGregorianCalendar(
              gregorianCalendar.get(java.util.GregorianCalendar.YEAR),
              gregorianCalendar.get(java.util.GregorianCalendar.MONTH) + 1,
              gregorianCalendar.get(java.util.GregorianCalendar.DAY_OF_MONTH),
              gregorianCalendar.get(java.util.GregorianCalendar.HOUR_OF_DAY),
              gregorianCalendar.get(java.util.GregorianCalendar.MINUTE),
              gregorianCalendar.get(java.util.GregorianCalendar.SECOND),
              gregorianCalendar.get(java.util.GregorianCalendar.MILLISECOND), 0);
    } catch (DatatypeConfigurationException e) {
      String error = String.format(
              "DatatypeConfigurationException thrown while building dateTime for now with msg = %s", e.getMessage());
      log.error(error);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR,
              String.format("%s%s", EXCEPTION_ACKNOWLEGDING_RECEIPT, error));
    }
    return result;
  }

  /**
   * This method will parse the received InputStream and build a list of CaseFeedbacks
   * @param fileContents to parse for CaseFeedbacks
   * @return a list of CaseFeedbacks
   */
  private List<CaseFeedback> parseIt(InputStream fileContents) throws CTPException {
    List<CaseFeedback> result = new ArrayList<>();
    InputStreamReader reader = new InputStreamReader(fileContents);
    try {
      CSVParser parser = new CSVParser(reader,
              CSVFormat.EXCEL.withHeader(CASE_REF, RESPONSE_DATE_TIME).withSkipHeaderRecord(true));
      List<CSVRecord> csvRecords = parser.getRecords();
      for (CSVRecord csvRecord: csvRecords) {
        result.add(buildCaseFeedback(csvRecord));
      }
    } catch (IOException e) {
      String error = String.format(
              "IOException thrown while parsing file contents with msg = %s", e.getMessage());
      log.error(error);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR,
              String.format("%s%s", EXCEPTION_ACKNOWLEGDING_RECEIPT, error));
    } catch (ParseException e) {
      // TODO
    } catch (DatatypeConfigurationException e) {
      // TODO
    }

    return result;
  }

  private static CaseFeedback buildCaseFeedback(CSVRecord csvRecord) throws ParseException,
          DatatypeConfigurationException {
    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedback.setCaseRef(csvRecord.get(CASE_REF));
    caseFeedback.setInboundChannel(InboundChannel.PAPER);
    String dateTimeStr = csvRecord.get(RESPONSE_DATE_TIME);
    caseFeedback.setResponseDateTime(stringToXMLGregorianCalendar(dateTimeStr));
    return caseFeedback;
  }

  private static XMLGregorianCalendar stringToXMLGregorianCalendar(String s)
          throws ParseException,
          DatatypeConfigurationException
  {
    XMLGregorianCalendar result = null;
    Date date;
    SimpleDateFormat simpleDateFormat;
    GregorianCalendar gregorianCalendar;

    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    date = simpleDateFormat.parse(s);
    gregorianCalendar =
            (GregorianCalendar)GregorianCalendar.getInstance();
    gregorianCalendar.setTime(date);
    result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    return result;
  }
}
