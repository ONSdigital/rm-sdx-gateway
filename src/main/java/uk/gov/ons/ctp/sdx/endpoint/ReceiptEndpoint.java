package uk.gov.ons.ctp.sdx.endpoint;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

/**
 * The endpoint to receive notifications from SDX
 */
@RestController
@RequestMapping(value = "/questionnairereceipts", produces = "application/json")
@Slf4j
public class ReceiptEndpoint {

  @Autowired
  private ReceiptService receiptService;

  @Autowired
  private MapperFacade mapperFacade;

  /**
   * This receives a receipt and forwards it to the ReceiptService for
   * acknowledgment.
   *
   * @param receiptDTO the receipt to be acknowledged
   * @return 201 if successful
   * @throws CTPException if invalid receipt or if it can't be acknowledged
   */
  @RequestMapping(method = RequestMethod.POST)
  public final ResponseEntity<?> acknowledge(final @Valid @RequestBody ReceiptDTO receiptDTO,
      UriComponentsBuilder builder) throws CTPException {
    log.debug("Entering acknowledge with receipt {}", receiptDTO);
    Receipt receipt = mapperFacade.map(receiptDTO, Receipt.class);
    receipt.setInboundChannel(InboundChannel.ONLINE);

    receiptService.acknowledge(receipt);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(builder.path("/questionnairereceipts/" + receipt.getCaseRef()).build().toUri());
    return ResponseEntity.created(headers.getLocation()).body(receiptDTO);
  }

}
