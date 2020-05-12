package uk.gov.ons.ctp.sdx.service;

import java.io.InputStream;
import java.util.List;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

/** The service interface to parse csf files */
public interface FileParser {
  /**
   * This method will parse the received InputStream and build a list of CaseReceipts.
   *
   * @param fileContents to parse for CaseReceipts
   * @return a list of CaseReceipts
   * @throws CTPException if invalid file provided
   */
  List<CaseReceipt> parseIt(InputStream fileContents) throws CTPException;
}
