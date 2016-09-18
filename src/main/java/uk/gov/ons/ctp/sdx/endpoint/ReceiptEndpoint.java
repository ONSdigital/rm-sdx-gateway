package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.error.CTPException;

@Slf4j
@RestController
public class ReceiptEndpoint {

  private static final int MIN_CASE_REF = 1;
  private static final String INVALID_RECEIPT = "The receipt provided is invalid.";

  // TODO IS 204 ok on positive scenario? --> I emailed Neville on 29/09.
  @RequestMapping(value = "/questionnairereceipts", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<?> acknowledge(@RequestBody Receipt receipt) throws CTPException {
    log.debug("acknowledging receipt {}", receipt);
    if (!validate(receipt)) {
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED, INVALID_RECEIPT);
    }
    // TODO Put message on queue to Case service
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private boolean validate(Receipt receipt) {
    boolean result = false;
    if (receipt != null) {
      Integer caseRef =  receipt.getCaseRef();
      if (caseRef != null) {
        try {
          int caseRefValue = caseRef.intValue();
          if (caseRefValue >= MIN_CASE_REF) {
            result = true;
          }
        } catch (NumberFormatException e) {
          log.error("NumberFormatException thrown while validating caseRef with msg = {}", e.getMessage());
        }
      }
    }
    return result;
  }
}
