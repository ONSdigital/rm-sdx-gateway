package uk.gov.ons.ctp.sdx.service;

import uk.gov.ons.ctp.sdx.domain.Receipt;

/**
 * The service to receive receipts
 */
public interface ReceiptService {
  /**
   * To acknowledge receipts
   * @param receipt to be acknowledged
   */
  void acknowledge(Receipt receipt);
}
