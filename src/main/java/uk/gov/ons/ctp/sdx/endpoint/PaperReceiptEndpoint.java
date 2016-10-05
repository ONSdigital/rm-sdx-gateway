package uk.gov.ons.ctp.sdx.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.glassfish.jersey.media.multipart.FormDataParam;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.net.URI;

/**
 * The endpoint to receive paper responses from Newport
 */
@Slf4j
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("/paperquestionnairereceipts")
public class PaperReceiptEndpoint {

  @Inject
  private ReceiptService receiptService;

  @Inject
  private MapperFacade mapperFacade;

  @Context
  UriInfo uriInfo;

  /**
   * This receives a file containing paper responses.
   *
   * @param fileContents the daily file received from Newport for paper responses
   * @return 201 if successful
   * @throws CTPException if the file can't be ingested
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public final Response acknowledgeFile(@FormDataParam("file") InputStream fileContents) throws CTPException {
    log.debug("Entering acknowledgeFile");
    receiptService.acknowledgeFile(fileContents);

    URI receiptUri = null;
    return Response.created(receiptUri).build();
  }
}
