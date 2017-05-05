package uk.gov.ons.ctp.sdx.endpoint;

import java.io.InputStream;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

/**
 * The endpoint to receive paper responses from Newport
 */
@Slf4j
@RestController
@RequestMapping(value = "/questionnairereceipts", produces = "application/json")
public class PaperReceiptEndpoint {

  @Autowired
  private ReceiptService receiptService;

  /**
   * This receives a file containing paper responses.
   *
   * @param fileContents the daily file received from Newport for paper responses
   * @return 201 if successful
   * @throws CTPException if the file can't be ingested
   */
  @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
  public final ResponseEntity<?> acknowledgeFile(@RequestParam("file") @RequestBody InputStream fileContents) throws CTPException {
    log.debug("Entering acknowledgeFile");
    receiptService.acknowledgeFile(fileContents);

    return ResponseEntity.created(URI.create("TODO")).build();
  }
}
