package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.error.CTPException;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

/**
 * The endpoint to receive notifications from SDX
 */
@Slf4j
@RestController
public class ReceiptEndpoint {

  public static final String INVALID_RECEIPT = "The receipt provided is invalid.";

  private static final int MIN_CASE_REF = 1;

  @Autowired
  private ReceiptService receiptService;

  // TODO IS 204 ok on positive scenario? --> I emailed Neville on 29/09.
  /**
   * The endpoint to receipt questionnaires
   * @param receipt the receipt
   * @return 204 if successful
   * @throws CTPException if invalid receipt provided
   */
  @RequestMapping(value = "/questionnairereceipts", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<?> acknowledge(@RequestBody Receipt receipt) throws CTPException {
    log.debug("acknowledging receipt {}", receipt);
    if (!validate(receipt)) {
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED, INVALID_RECEIPT);
    }
    // TODO Put message on queue to Case service

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * To validate the receipt
   * @param receipt the receipt to validate
   * @return true if valid receipt
   */
  private boolean validate(Receipt receipt) {
    boolean result = false;
    if (receipt != null) {
      Integer caseRef =  receipt.getCaseRef();
      if (caseRef != null && caseRef >= MIN_CASE_REF) {
        result = true;
      }
    }
    return result;
  }
}
