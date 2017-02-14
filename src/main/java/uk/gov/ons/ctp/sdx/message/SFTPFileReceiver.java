package uk.gov.ons.ctp.sdx.message;

import java.io.InputStream;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

import uk.gov.ons.ctp.common.error.CTPException;

/**
 * Service for receiving files from SFTP and then sending back to flow to rename
 */
public interface SFTPFileReceiver {
  
  /**
   * receives a stream of file from SFTP and then processes file.
   * @param message containing stream of file from SFTP.
   * @return message read in.
   */
  Message<InputStream> processFile(Message<InputStream> message) throws CTPException;

  void sftpSuccessProcess(GenericMessage<GenericMessage<byte[]>> message);

  void sftpFailedProcess(GenericMessage<MessagingException> message);

}