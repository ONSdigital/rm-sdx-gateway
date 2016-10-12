package uk.gov.ons.ctp.sdx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.service.FileParser;
import uk.gov.ons.ctp.sdx.utility.DateUtils;

import javax.inject.Named;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
  private String caseRefColName;
  @Value("${RESPONSE_DATE_TIME_COL_NAME}")
  private String responseDateTimeColName;
  @Value("${RESPONSE_DATE_TIME_COL_FORMAT}")
  private String responseDateTimeColFormat;

  /**
   * This method will parse the received InputStream and build a list of CaseReceipts.
   * @param fileContents to parse for CaseReceipts
   * @return a list of CaseReceipts
   * @throws CTPException if invalid file provided
   */
  public List<CaseReceipt> parseIt(InputStream fileContents) throws CTPException {
    log.debug("parseIt {}", fileContents);
    List<CaseReceipt> result = new ArrayList<>();
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
            result.add(buildCaseReceipt(csvRecord));
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
   * To build a CaseReceipt from a CSVRecord
   * @param csvRecord the CSVRecord
   * @return the corresponding CaseReceipt
   * @throws DatatypeConfigurationException when a CaseReceipt cannot be built
   */
  private CaseReceipt buildCaseReceipt(CSVRecord csvRecord) throws DatatypeConfigurationException {
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(csvRecord.get(caseRefColName));
    caseReceipt.setInboundChannel(InboundChannel.PAPER);
    String dateTimeStr = csvRecord.get(responseDateTimeColName);
    caseReceipt.setResponseDateTime(DateUtils.stringToXMLGregorianCalendar(dateTimeStr, responseDateTimeColFormat));
    return caseReceipt;
  }
}
