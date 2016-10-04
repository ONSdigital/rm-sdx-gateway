package uk.gov.ons.ctp.sdx.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.domain.Receipt;

/**
 * The service to acknowleged receipts
 */
public interface ReceiptService {
  /**
   * To acknowledge receipts
   * @param receipt to be acknowledged
   * @throws CTPException if invalid receipt or if it can't be acknowledged
   */
  void acknowledge(Receipt receipt) throws CTPException;
}
