package uk.gov.ons.ctp.sdx.message;

import uk.gov.ons.ctp.sdx.quarantine.common.CTPException;

/** Service for receiving files from SFTP and then sending back to flow to rename */
public interface SFTPPaperReceiptReceiver {

  /**
   * receives a stream of file from SFTP and then processes file.
   *
   * @throws CTPException
   */
  void consumePaperReceipt() throws CTPException;
}