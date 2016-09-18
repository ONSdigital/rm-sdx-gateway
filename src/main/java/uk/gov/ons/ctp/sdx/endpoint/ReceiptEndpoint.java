package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.sdx.domain.Receipt;

import javax.validation.Valid;

@Slf4j
@RestController
public class ReceiptEndpoint {

  // TODO IS 204 ok on positive scenario? --> I emailed Neville on 29/09.
  @RequestMapping(value = "/questionnairereceipts", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<?> acknowledge(@RequestBody @Valid Receipt receipt) {
    log.debug("acknowledging receipt {}", receipt);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
