package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The endpoint to receive notifications from SDX
 */
@Slf4j
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("/questionnairereceipts")
public class ReceiptEndpoint {

  public static final String INVALID_RECEIPT = "The receipt provided is invalid.";

  private static final int MIN_CASE_REF = 1;

  @Inject
  private ReceiptService receiptService;

  @Inject
  private MapperFacade mapperFacade;

  @POST
  public final ReceiptDTO acknowledge(final @Valid ReceiptDTO receiptDTO) throws CTPException {
    log.debug("Entering acknowledge with receipt {}", receiptDTO);
    receiptService.acknowledge(mapperFacade.map(receiptDTO, Receipt.class));
    return null;  // TODO IS 204 ok on positive scenario? --> I emailed Neville on 29/09.
  }
}
