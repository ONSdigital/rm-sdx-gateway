package uk.gov.ons.ctp.sdx.message.impl;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.integration.annotation.Publisher;

import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.ReceiptPublisher;

// TODO change the stereotype below to an integration-specific one
@Service
@Slf4j
public class ReceiptPublisherImpl implements ReceiptPublisher{
  //@Publisher(channel = "actionFeedbackOutbound")
  @Override
  public Receipt sendReceipt(Receipt receipt) {
    log.debug("sending to queue receipt {}", receipt);
    return null;
  }
}
