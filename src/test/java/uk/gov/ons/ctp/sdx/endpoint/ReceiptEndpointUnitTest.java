package uk.gov.ons.ctp.sdx.endpoint;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.sdx.BeanMapper;
import uk.gov.ons.ctp.sdx.utility.MockReceiptServiceFactory;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

public class ReceiptEndpointUnitTest extends CTPJerseyTest {

  private static final String RECEIPT_INVALIDJSON_SCENARIO1 = "{\"random\":  \"abc\"}";
  private static final String RECEIPT_INVALIDJSON_SCENARIO2 = "{\"caseRef\":  \"\"}";
  private static final String RECEIPT_VALIDJSON = "{\"caseRef\":  \"abc\"}";
  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(ReceiptEndpoint.class, ReceiptService.class, MockReceiptServiceFactory.class, new BeanMapper(),
            new CTPMessageBodyReader<ReceiptDTO>(ReceiptDTO.class) { });
  }

  @Test
  public void acknowledgeReceiptGoodJsonProvided() {
    with("http://localhost:9998/questionnairereceipts").post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_VALIDJSON)
            .assertResponseCodeIs(HttpStatus.CREATED)
            .assertStringInBody("$.caseRef", "abc")
            .andClose();
    // TODO verify header Location
  }

  @Test
  public void acknowledgeReceiptBadJsonProvidedScenario1() {
    with("http://localhost:9998/questionnairereceipts").post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_INVALIDJSON_SCENARIO1)
            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
            .assertTimestampExists()
            .assertMessageEquals("Provided json is incorrect.")
            .andClose();
  }

  @Test
  public void acknowledgeReceiptBadJsonProvidedScenario2() {
    with("http://localhost:9998/questionnairereceipts").post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_INVALIDJSON_SCENARIO2)
            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
            .assertTimestampExists()
            .assertMessageEquals("Provided json fails validation.")
            .andClose();
  }
}
