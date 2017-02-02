package uk.gov.ons.ctp.sdx.message;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.messaging.Message;

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
  Message<InputStream> processFile(Message<InputStream> message) throws CTPException, IOException;
}
