package uk.gov.ons.ctp.sdx.message;

import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;

/**
 * The publisher to queues
 */
public interface CaseFeedbackPublisher {
  /**
   * To publish a caseFeedback to queue
   * @param caseFeedback to be published
   * @return the published caseFeedback
   */
  CaseFeedback send(CaseFeedback caseFeedback);
}
