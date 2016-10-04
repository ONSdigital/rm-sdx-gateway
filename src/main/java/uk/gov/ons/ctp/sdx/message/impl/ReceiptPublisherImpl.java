package uk.gov.ons.ctp.sdx.message.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.ReceiptPublisher;

import javax.inject.Named;

@Slf4j
@Named
public class ReceiptPublisherImpl implements ReceiptPublisher{
  //@Publisher(channel = "actionFeedbackOutbound")
  @Override
  public Receipt sendReceipt(Receipt receipt) {
    log.debug("sending to queue receipt {}", receipt);
    return null;
  }
}
