package uk.gov.ons.ctp.sdx.message.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseFeedbackPublisher;

import javax.inject.Named;

@Slf4j
@Named
public class CaseFeedbackPublisherImpl implements CaseFeedbackPublisher {
  @Override
  public CaseFeedback send(CaseFeedback caseFeedback) {
    log.debug("sending to queue caseFeedback {}", caseFeedback);
    // TODO
    return null;
  }
}
