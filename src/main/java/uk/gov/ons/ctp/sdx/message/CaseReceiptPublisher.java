package uk.gov.ons.ctp.sdx.message;

import uk.gov.ons.ctp.sdx.quarantine.casesvc.CaseReceipt;

/** The publisher to queues */
public interface CaseReceiptPublisher {
  /**
   * To publish a caseReceipt to queue
   *
   * @param caseReceipt to be published
   */
  void send(CaseReceipt caseReceipt);
}
