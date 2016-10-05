package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * The endpoint to receive notifications from SDX
 */
@Slf4j
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("/questionnairereceipts")
public class ReceiptEndpoint {

  @Inject
  private ReceiptService receiptService;

  @Inject
  private MapperFacade mapperFacade;

  @Context
  UriInfo uriInfo;

  /**
   * This receives a receipt and forwards it to the ReceiptService for acknowledgment.
   *
   * @param receiptDTO the receipt to be acknowledged
   * @return 201 if successful
   * @throws CTPException if invalid receipt or if it can't be acknowledged
   */
  @POST
  public final Response acknowledge(final @Valid ReceiptDTO receiptDTO) throws CTPException {
    log.debug("Entering acknowledge with receipt {}", receiptDTO);
    Receipt receipt = mapperFacade.map(receiptDTO, Receipt.class);
    receipt.setInboundChannel(InboundChannel.ONLINE);

    receiptService.acknowledge(receipt);

    UriBuilder ub = uriInfo.getAbsolutePathBuilder();
    URI receiptUri = ub.path(receipt.getCaseRef()).build();
    return Response.created(receiptUri).entity(receiptDTO).build();
  }

}
