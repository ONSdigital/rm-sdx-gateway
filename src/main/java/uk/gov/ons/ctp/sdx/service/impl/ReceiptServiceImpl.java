package uk.gov.ons.ctp.sdx.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseFeedbackPublisher;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Named
public class ReceiptServiceImpl implements ReceiptService {

  @Inject
  private CaseFeedbackPublisher caseFeedbackPublisher;

  @Override
  public void acknowledge(Receipt receipt) {
    log.debug("acknowledging receipt {}", receipt);
    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedback.setCaseRef(receipt.getCaseRef());
    // TODO inboundchannel and datetime
    caseFeedbackPublisher.send(caseFeedback);
  }
}
