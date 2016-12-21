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

import static uk.gov.ons.ctp.sdx.service.ReceiptServiceImplTest.CASE_REF;

public class ReceiptEndpointUnitTest extends CTPJerseyTest {

  private static final String LOCATION = "Location";
  private static final String RECEIPT_INVALIDJSON_SCENARIO1 = "{\"random\":  \"abc\"}";
  private static final String RECEIPT_INVALIDJSON_SCENARIO2 = "{\"caseRef\":  \"\"}";
  private static final String RECEIPT_VALIDJSON = String.format("{\"caseRef\":  \"%s\"}", CASE_REF);
  private static final String SERVER_URL = "/questionnairereceipts";
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
    with(SERVER_URL).post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_VALIDJSON)
            .assertResponseCodeIs(HttpStatus.CREATED)
            .assertStringInBody("$.caseRef", CASE_REF)
            .assertHeader(LOCATION, String.format("http://localhost:%d%s/%s", getPort(), SERVER_URL, CASE_REF))
            .andClose();
  }

  @Test
  public void acknowledgeReceiptBadJsonProvidedScenario1() {
    with(SERVER_URL).post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_INVALIDJSON_SCENARIO1)
            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
            .assertTimestampExists()
            .assertMessageEquals("Provided json is incorrect.")
            .andClose();
  }

  @Test
  public void acknowledgeReceiptBadJsonProvidedScenario2() {
    with(SERVER_URL).post(MediaType.APPLICATION_JSON_TYPE, RECEIPT_INVALIDJSON_SCENARIO2)
            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
            .assertTimestampExists()
            .assertMessageEquals("Provided json fails validation.")
            .andClose();
  }
}
