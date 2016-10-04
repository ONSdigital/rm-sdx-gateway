package uk.gov.ons.ctp.sdx.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Publisher;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.sdx.message.CaseFeedbackPublisher;

import javax.inject.Named;

/**
 * The publisher to queues
 */
@Slf4j
@Named
public class CaseFeedbackPublisherImpl implements CaseFeedbackPublisher {
  @Publisher(channel = "caseFeedbackOutbound")
  @Override
  public CaseFeedback send(CaseFeedback caseFeedback) {
    log.debug("send to queue caseFeedback {}", caseFeedback);
    return caseFeedback;
  }
}
