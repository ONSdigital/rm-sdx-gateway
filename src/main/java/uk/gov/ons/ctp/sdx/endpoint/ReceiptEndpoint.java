package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.ons.ctp.sdx.domain.Receipt;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/questionnairereceipts")
public class ReceiptEndpoint {

  @RequestMapping(method= RequestMethod.POST)
  public ResponseEntity<?> acknowledge(@RequestBody @Valid Receipt receipt) {
    log.debug("acknowledging receipt {}", receipt);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
