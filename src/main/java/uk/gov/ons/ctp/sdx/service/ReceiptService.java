package uk.gov.ons.ctp.sdx.service;

import java.io.InputStream;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.quarantine.common.CTPException;

/** The service to acknowledge receipts */
public interface ReceiptService {
  /**
   * To acknowledge individual receipts
   *
   * @param receipt to be acknowledged
   * @throws CTPException if invalid receipt or if it can't be acknowledged
   */
  void acknowledge(Receipt receipt) throws CTPException;

  /**
   * To acknowledge a file of receipts (only used in Census)
   *
   * @param fileContents the file containing all receipts to be acknowledged
   * @throws CTPException if invalid file provided
   */
  void acknowledgeFile(InputStream fileContents) throws CTPException;
}
