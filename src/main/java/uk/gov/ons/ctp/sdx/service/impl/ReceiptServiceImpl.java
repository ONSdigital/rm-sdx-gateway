package uk.gov.ons.ctp.sdx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.ReceiptPublisher;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Named
public class ReceiptServiceImpl implements ReceiptService {

  @Inject
  private ReceiptPublisher receiptPublisher;

  @Override
  public void acknowledge(Receipt receipt) {
    log.debug("acknowledging receipt {}", receipt);
    // TODO any other action required?
    receiptPublisher.sendReceipt(receipt);
  }
}
