package uk.gov.ons.ctp.sdx.service.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;


import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.service.ReceiptService;


@MessageEndpoint
@Slf4j
public class SFTPfile {
  
  @Inject
  private ReceiptService receiptService;
  
  @ServiceActivator(inputChannel = "sftpInbound")
  public void processFile(Message<InputStream> message) throws CTPException, IOException {
    log.debug("Entering acknowledgeFile");
    receiptService.acknowledgeFile(message.getPayload());

    Closeable closeable = new IntegrationMessageHeaderAccessor(message).getCloseableResource();
    log.debug("closing");
    if (closeable != null) {
      closeable.close();
      log.debug("closed");
    }
  }
}