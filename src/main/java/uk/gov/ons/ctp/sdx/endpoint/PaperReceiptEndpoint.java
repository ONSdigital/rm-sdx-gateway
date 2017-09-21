package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import java.io.IOException;
import java.net.URI;

/**
 * The endpoint to receive paper responses from Newport
 */
@Slf4j
@RestController
@RequestMapping(value = "/paperquestionnairereceipts", produces = "application/json")
public class PaperReceiptEndpoint {

  @Autowired
  private ReceiptService receiptService;

  /**
   * This receives a file containing paper responses.
   *
   * @param file the daily file received from Newport for paper responses
   * @return 201 if successful
   * @throws CTPException if the file can't be ingested
   */
  @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
  public final ResponseEntity<?> acknowledgeFile(@RequestParam("file") MultipartFile file) throws CTPException {
    log.debug("Entering acknowledgeFile");
    try {
      receiptService.acknowledgeFile(file.getInputStream());
    } catch (IOException e) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "Failed reading the provided file.");
    }

    return ResponseEntity.created(URI.create("TODO")).build();
  }
}
