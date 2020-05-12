package uk.gov.ons.ctp.sdx.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.service.FileParser;

/** The service implementation to parse csv files */
@Slf4j
@Component
public class FileParserImpl implements FileParser {

  public static final String EXCEPTION_NO_RECORDS = "No record found.";

  private static final Integer NB_EXPECTED_COLUMNS = 2;
  private static final String EXCEPTION_PARSING_RECORD =
      "An unexpected error occured while parsing a paper receipt record.";

  @Value("${RESPONSE_DATE_TIME_COL_FORMAT}")
  private String responseDateTimeColFormat;

  /**
   * This method will parse the received InputStream and build a list of CaseReceipts.
   *
   * @param fileContents to parse for CaseReceipts
   * @return a list of CaseReceipts
   * @throws CTPException if invalid file provided
   */
  public List<CaseReceipt> parseIt(InputStream fileContents) throws CTPException {
    log.debug("parseIt {}", fileContents);
    List<CaseReceipt> result = new ArrayList<>();
    InputStreamReader reader = new InputStreamReader(fileContents);
    CSVParser parser = null;
    try {
      parser = new CSVParser(reader, CSVFormat.EXCEL);
      List<CSVRecord> csvRecords = parser.getRecords();
      if (!validate(csvRecords)) {
        throw new CTPException(CTPException.Fault.VALIDATION_FAILED, EXCEPTION_NO_RECORDS);
      }
      for (CSVRecord csvRecord : csvRecords) {
        log.debug("dealing with csvRecord {}", csvRecord);
        if (validate(csvRecord)) {
          try {
            result.add(buildCaseReceipt(csvRecord));
          } catch (DatatypeConfigurationException e) {
            log.error(String.format("%s%s", EXCEPTION_PARSING_RECORD, e.getMessage()));
            log.error("Stack trace: " + e);
          }
        }
      }
    } catch (IOException e) {
      String error =
          String.format(
              "IOException thrown while parsing file contents with msg = %s", e.getMessage());
      log.error(error);
      log.error("Stack trace: " + e);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, error);
    } finally {
      if (parser != null) {
        try {
          parser.close();
        } catch (IOException e) {
          String error =
              String.format(
                  "IOException thrown while closing the parser with msg = %s", e.getMessage());
          log.error(error);
          log.error("Stack trace: " + e);
        }
      }
    }

    return result;
  }

  /**
   * To validate the list of csvRecords
   *
   * @param csvRecordList the list of csvRecords
   * @return true if at least 1 csvRecord is valid
   */
  private boolean validate(List<CSVRecord> csvRecordList) {
    boolean result;
    if (csvRecordList == null || csvRecordList.isEmpty()) {
      result = false;
    } else {
      int countValidCsvRecords = 0;
      for (CSVRecord csvRecord : csvRecordList) {
        if (validate(csvRecord)) {
          countValidCsvRecords += 1;
        }
      }
      result = countValidCsvRecords != 0;
    }
    return result;
  }

  /**
   * To validate a CSVRecord
   *
   * @param csvRecord to be validated
   * @return true if csvRecord is valid
   */
  private boolean validate(CSVRecord csvRecord) {
    return csvRecord.isConsistent() && csvRecord.size() == NB_EXPECTED_COLUMNS;
  }

  /**
   * To build a CaseReceipt from a CSVRecord
   *
   * @param csvRecord the CSVRecord
   * @return the corresponding CaseReceipt
   * @throws DatatypeConfigurationException when a CaseReceipt cannot be built
   */
  private CaseReceipt buildCaseReceipt(CSVRecord csvRecord) throws DatatypeConfigurationException {
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setInboundChannel(InboundChannel.PAPER);
    String dateTimeStr = csvRecord.get(0);
    caseReceipt.setResponseDateTime(
        DateTimeUtil.stringToXMLGregorianCalendar(dateTimeStr, responseDateTimeColFormat));
    caseReceipt.setCaseRef(csvRecord.get(1));
    return caseReceipt;
  }
}
