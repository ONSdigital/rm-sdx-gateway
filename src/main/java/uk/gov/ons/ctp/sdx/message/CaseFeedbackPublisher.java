package uk.gov.ons.ctp.sdx.message;

import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;

/**
 * The publisher to queues
 */
public interface CaseFeedbackPublisher {
  CaseFeedback send(CaseFeedback caseFeedback);
}
