package uk.gov.ons.ctp.sdx.endpoint;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jaxrs.CTPXmlMessageBodyReader;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.sdx.BeanMapper;
import uk.gov.ons.ctp.sdx.endpoint.utility.MockReceiptServiceFactory;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

public class ReceiptEndpointUnitTest extends CTPJerseyTest {

  private static final String RECEIPT_INVALIDJSON = "{\"random\":  \"abc\"}";
  private static final String RECEIPT_VALIDJSON = "{\"caseRef\":  \"abc\"}";
  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(ReceiptEndpoint.class, ReceiptService.class, MockReceiptServiceFactory.class, new BeanMapper(),
            new CTPXmlMessageBodyReader<ReceiptDTO>(ReceiptDTO.class) { });
  }

  @Test
  public void acknowledgeReceiptGoodJsonProvided() {
    with("http://localhost:9998/questionnairereceipts").post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_VALIDJSON)
            .assertResponseCodeIs(HttpStatus.NO_CONTENT)
            .andClose();
  }

  // TODO UNcomment the lines below
  @Test
  public void acknowledgeReceiptBadJsonProvided() {
    with("http://localhost:9998/questionnairereceipts").post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_INVALIDJSON)
            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
//            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
//            .assertTimestampExists()
//            .assertMessageEquals("Provided json is incorrect.")
            .andClose();
  }
}
