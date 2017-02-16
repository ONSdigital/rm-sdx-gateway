package uk.gov.ons.ctp.sdx.message.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.sdx.message.SFTPPaperReceiptReciever;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

@Slf4j
@Named
@Configuration
public class SFTPPaperReceiptRecieverImpl implements SFTPPaperReceiptReciever {

  private static final String SFTP_LOCK = "sftpLock";

  @Value("${sftp.host}")
  private String sftpHost;

  @Value("${sftp.port}")
  private int sftpPort;

  @Value("${sftp.username}")
  private String sftpUserName;

  @Value("${sftp.password}")
  private String sftpPassword;

  @Value("${sftp.directory}")
  private String dir;

  @Value("${sftp.filepattern}")
  private String filepattern;

  @Inject
  private ReceiptService receiptService;

  @Inject
  private DistributedLockManager sdxLockManager;

  @Scheduled(cron = "${sftp.cron}")
  public void consumePaperReceipt() throws CTPException {

    if (!sdxLockManager.isLocked(SFTP_LOCK) && sdxLockManager.lock(SFTP_LOCK)) {
      log.debug("sftp is not locked");
      log.debug("Grabbed locked");

      try {
        JSch jsch = new JSch();
        Session session = jsch.getSession(sftpUserName, sftpHost, sftpPort);
        session.setPassword(sftpPassword);
        JSch.setConfig("StrictHostKeyChecking", "no");
        log.debug("Connecting");
        session.connect();
        log.debug("Connected");

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        sftp.cd(dir);

        Vector<LsEntry> fileList = sftp.ls(filepattern);

        processFiles(sftp, fileList);
        disconnectSession(session, sftp);
        
      } catch (SftpException | JSchException | IOException e) {
        throw new CTPException(Fault.SYSTEM_ERROR, e);
      } finally {
        sdxLockManager.unlock(SFTP_LOCK);
        log.debug("Released lock");
      }
    } else {
      log.debug("was already locked");
    }
  }

  private void disconnectSession(Session session, ChannelSftp sftp) {
    sftp.disconnect();
    session.disconnect();
    log.debug("Disconnected");
  }

  private void processFiles(ChannelSftp sftp, Vector<LsEntry> fileList)
      throws SftpException, CTPException, IOException {
    for (LsEntry lsEntry : fileList) {
      String file = lsEntry.getFilename();
      String newPath = file + ".processed";

      InputStream stream = sftp.get(file);
      try {
        receiptService.acknowledgeFile(stream);
        log.debug("Now processing file " + file);
        sftp.rename(file, newPath);
        log.debug("File renamed to " + newPath);
      } finally {
        stream.close();
      }
    }
  }
}
