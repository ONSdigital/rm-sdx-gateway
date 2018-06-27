package uk.gov.ons.ctp.sdx.message.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.sdx.config.AppConfig;
import uk.gov.ons.ctp.sdx.message.SFTPPaperReceiptReciever;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

/**
 * Ingest paper receipts placed in remote directory by fetching them via SFTP and passing them to
 * SDX gateway.
 */
@Slf4j
@MessageEndpoint
@Configuration
public class SFTPPaperReceiptRecieverImpl implements SFTPPaperReceiptReciever {

  private static final String SFTP_LOCK = "sftpLock";

  @Autowired private AppConfig appConfig;

  @Autowired private ReceiptService receiptService;

  @Autowired private DistributedLockManager sdxLockManager;

  /**
   * Ingest paper receipts placed in remote directory by fetching them via SFTP and passing them to
   * SDX gateway.
   *
   * @throws CTPException something went wrong
   */
  @Scheduled(cron = "${sftp.cron}")
  public void consumePaperReceipt() throws CTPException {
    if (!sdxLockManager.isLocked(SFTP_LOCK) && sdxLockManager.lock(SFTP_LOCK)) {
      try {
        Session session = getSession();
        ChannelSftp sftp = getSftp(session);

        Vector<LsEntry> fileList = sftp.ls(appConfig.getSftp().getFilepattern());

        processFiles(sftp, fileList);
        disconnectSession(session, sftp);
      } catch (SftpException | JSchException | IOException e) {
        throw new CTPException(Fault.SYSTEM_ERROR, e);
      } finally {
        sdxLockManager.unlock(SFTP_LOCK);
      }
    }
  }

  /**
   * Create and connect SFTP channel
   *
   * @param session a Jsch session to connect to remote host
   * @return sftp sftp channel to transfer paper receipts
   * @throws JSchException jshexception
   * @throws SftpException jshexception
   */
  private ChannelSftp getSftp(Session session) throws JSchException, SftpException {
    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
    sftp.connect();
    sftp.cd(appConfig.getSftp().getDirectory());
    return sftp;
  }

  /**
   * Create and connect SFTP channel
   *
   * @return session connection session to remote host
   * @throws JSchException jshexception
   */
  private Session getSession() throws JSchException {
    JSch jsch = new JSch();
    Session session =
        jsch.getSession(
            appConfig.getSftp().getUsername(),
            appConfig.getSftp().getHost(),
            appConfig.getSftp().getPort());
    session.setPassword(appConfig.getSftp().getPassword());
    JSch.setConfig("StrictHostKeyChecking", "no");
    session.connect();
    return session;
  }

  /**
   * Dissconnect sftp channel and session
   *
   * @param session session to dissconnect
   * @param sftp sftp to dissconnect
   */
  private void disconnectSession(Session session, ChannelSftp sftp) {
    sftp.disconnect();
    session.disconnect();
  }

  /**
   * Create and connect SFTP channel
   *
   * @throws JSchException jschexception
   * @throws IOException
   * @throws CTPException
   * @param sftp the sftp channel
   * @param fileList list of files to be processed
   */
  private void processFiles(ChannelSftp sftp, Vector<LsEntry> fileList)
      throws SftpException, CTPException, IOException {
    for (LsEntry lsEntry : fileList) {
      String file = lsEntry.getFilename();
      String newPath = file + ".processed" + System.currentTimeMillis();

      try (InputStream stream = sftp.get(file)) {
        receiptService.acknowledgeFile(stream);
        log.debug("Now processing file " + file);
        sftp.rename(file, newPath);
        log.debug("File renamed to " + newPath);
      }
    }
  }
}
