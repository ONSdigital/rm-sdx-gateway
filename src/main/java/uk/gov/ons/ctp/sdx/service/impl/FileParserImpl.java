package uk.gov.ons.ctp.sdx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.service.FileParser;

import javax.inject.Named;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *  The service implementation to parse csf files
 */
@Slf4j
@Named
public class FileParserImpl implements FileParser {

  public static final String EXCEPTION_NO_RECORDS = "No record found.";
  private static final String EXCEPTION_PARSING_RECORD =
          "An unexpected error occured while parsing a paper receipt record.";

  @Value("${CASE_REF_COL_NAME}")
  private String caseRefColName = "caseRef";
  @Value("${RESPONSE_DATE_TIME_COL_NAME}")
  private String responseDateTimeColName = "responseDateTime";

  /**
   * This method will parse the received InputStream and build a list of CaseFeedbacks.
   * @param fileContents to parse for CaseFeedbacks
   * @return a list of CaseFeedbacks
   * @throws CTPException if invalid file provided
   */
  public List<CaseFeedback> parseIt(InputStream fileContents) throws CTPException {
    log.debug("parseIt {}", fileContents);
    List<CaseFeedback> result = new ArrayList<>();
    InputStreamReader reader = new InputStreamReader(fileContents);
    try {
      CSVParser parser = new CSVParser(reader,
              CSVFormat.EXCEL.withHeader(caseRefColName, responseDateTimeColName).withSkipHeaderRecord(true));
      List<CSVRecord> csvRecords = parser.getRecords();
      if (csvRecords == null || csvRecords.isEmpty()) {
        throw new CTPException(CTPException.Fault.VALIDATION_FAILED, EXCEPTION_NO_RECORDS);
      }
      for (CSVRecord csvRecord: csvRecords) {
        log.debug("dealing with csvRecord {}", csvRecord);
        if (validate(csvRecord)) {
          try {
            result.add(buildCaseFeedback(csvRecord));
          } catch (Exception e) {
            log.error(String.format("%s%s", EXCEPTION_PARSING_RECORD, e.getMessage()));
          }
        }
      }
    } catch (IOException e) {
      String error = String.format(
              "IOException thrown while parsing file contents with msg = %s", e.getMessage());
      log.error(error);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, error);
    }

    return result;
  }

  /**
   * To validate a CSVRecord
   * @param csvRecord to be validated
   * @return true if csvRecord is valid
   */
  private boolean validate(CSVRecord csvRecord) {
    boolean result = false;
    if (csvRecord.isConsistent() && csvRecord.isSet(caseRefColName) && csvRecord.isSet(responseDateTimeColName)) {
      result = true;
    }
    return result;
  }

  /**
   * To build a CaseFeedback from a CSVRecord
   * @param csvRecord the CSVRecord
   * @return the corresponding CaseFeedback
   * @throws ParseException when the dateResponseTime can't be defined
   * @throws DatatypeConfigurationException when the dateResponseTime can't be defined
   */
  private CaseFeedback buildCaseFeedback(CSVRecord csvRecord) throws ParseException,
          DatatypeConfigurationException {
    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedback.setCaseRef(csvRecord.get(caseRefColName));
    caseFeedback.setInboundChannel(InboundChannel.PAPER);
    String dateTimeStr = csvRecord.get(responseDateTimeColName);
    caseFeedback.setResponseDateTime(stringToXMLGregorianCalendar(dateTimeStr));
    return caseFeedback;
  }

  /**
   * To transform a string into XMLGregorianCalendar
   * @param string the string to transform
   * @return the XMLGregorianCalendar
   * @throws ParseException when a XMLGregorianCalendar cannot be built
   * @throws DatatypeConfigurationException when a XMLGregorianCalendar cannot be built
   */
  public static XMLGregorianCalendar stringToXMLGregorianCalendar(String string)
          throws ParseException,
          DatatypeConfigurationException {
    XMLGregorianCalendar result = null;
    Date date;
    SimpleDateFormat simpleDateFormat;
    GregorianCalendar gregorianCalendar;

    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    date = simpleDateFormat.parse(string);
    gregorianCalendar =
            (GregorianCalendar)GregorianCalendar.getInstance();
    gregorianCalendar.setTime(date);
    result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    return result;
  }
}
