package uk.gov.ons.ctp.sdx.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;

import java.io.InputStream;
import java.util.List;

public interface FileParser {
  /**
   * This method will parse the received InputStream and build a list of CaseFeedbacks.
   * @param fileContents to parse for CaseFeedbacks
   * @return a list of CaseFeedbacks
   */
  List<CaseFeedback> parseIt(InputStream fileContents) throws CTPException;
}
