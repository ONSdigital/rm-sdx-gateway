package uk.gov.ons.ctp.sdx.message;

import uk.gov.ons.ctp.sdx.domain.Receipt;

/**
 * The publisher to queues
 */
public interface ReceiptPublisher {
  /**
   * To send a receipt to a queue
   * @param receipt to be put on a queue
   * @return the receipt that will end up on a queue
   */
  Receipt sendReceipt(Receipt receipt);
}
