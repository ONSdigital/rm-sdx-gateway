package uk.gov.ons.ctp.sdx.message.impl;

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
import uk.gov.ons.ctp.sdx.message.SFTPFileReceiver;
import uk.gov.ons.ctp.sdx.service.ReceiptService;


@MessageEndpoint
@Slf4j
public class SFTPFileReceiverImpl implements SFTPFileReceiver{
  
  @Inject
  private ReceiptService receiptService;
  
  @Override
  @ServiceActivator(inputChannel = "sftpInbound", outputChannel="sftpStreamTransformer")
  public Message<InputStream> processFile(Message<InputStream> message) throws CTPException {
    log.debug("Entering acknowledgeFile");
    receiptService.acknowledgeFile(message.getPayload());
    
    Closeable closeable = new IntegrationMessageHeaderAccessor(message).getCloseableResource();
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        log.error("IOException thrown while closing Message Stream...", e.getMessage());
        throw new CTPException(CTPException.Fault.SYSTEM_ERROR, e.getMessage());
      }
    }
    return message;
  }

}