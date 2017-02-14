package uk.gov.ons.ctp.sdx.message.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.message.SFTPFileReceiver;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

@Slf4j
@MessageEndpoint
public class SFTPFileReceiverImpl implements SFTPFileReceiver {

  @Inject
  private ReceiptService receiptService;

  @Inject
  private DistributedLockManager sdxLockManager;

  @Override
  @ServiceActivator(inputChannel = "sftpInbound", outputChannel = "sftpStreamTransformer")
  public Message<InputStream> processFile(Message<InputStream> message) throws CTPException {
    String filename = (String) message.getHeaders().get("file_remoteFile");
    if (!sdxLockManager.isLocked(filename) && sdxLockManager.lock(filename)) {
      log.debug(filename + ": is not locked");
      receiptService.acknowledgeFile(message.getPayload());
      log.debug(filename + ": grabbed locked");
    } else {
      log.debug(filename + ": was already locked");
      Closeable closeable = new IntegrationMessageHeaderAccessor(message).getCloseableResource();
      if (closeable != null) {
        try {
          closeable.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing Message Stream...", e.getMessage());
          throw new CTPException(CTPException.Fault.SYSTEM_ERROR, e.getMessage());
        }
      }

      message = null;
    }
    return message;
  }

  /**
   * Using JPA entities to update repository for actionIds exported was slow.
   * JPQL queries used for performance reasons. To increase performance updates
   * batched with IN clause.
   *
   * @param message
   *          Spring integration message sent
   */
  @Override
  @ServiceActivator(inputChannel = "sftpSuccessProcess")
  public void sftpSuccessProcess(GenericMessage<GenericMessage<byte[]>> message) {
    String filename = (String) message.getPayload().getHeaders().get("file_remoteFile");
    log.debug("sftpSuccessProcess: " + filename);
    sdxLockManager.unlock(filename);
  }

  @Override
  @ServiceActivator(inputChannel = "sftpFailedProcess")
  public void sftpFailedProcess(GenericMessage<MessagingException> message) {
    String filename = (String) message.getPayload().getFailedMessage().getHeaders().get("file_remoteFile");
    log.debug("sftpFailedProcess: " + filename);
    sdxLockManager.unlock(filename);
  }

}