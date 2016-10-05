package uk.gov.ons.ctp.sdx.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.domain.Receipt;

import java.io.InputStream;

/**
 * The service to acknowledge receipts
 */
public interface ReceiptService {
  /**
   * To acknowledge individual receipts
   * @param receipt to be acknowledged
   * @throws CTPException if invalid receipt or if it can't be acknowledged
   */
  void acknowledge(Receipt receipt) throws CTPException;

  /**
   * To acknowledge a file of receipts
   * @param fileContents the file containing all receipts to be acknowledged
   * @throws CTPException if invalid file provided
   */
  void acknowledgeFile(InputStream fileContents) throws CTPException;
}
