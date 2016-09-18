package uk.gov.ons.ctp.sdx.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.sdx.Application;
import uk.gov.ons.ctp.sdx.domain.Receipt;

import static org.assertj.core.api.BDDAssertions.then;
import static uk.gov.ons.ctp.sdx.endpoint.ReceiptEndpoint.INVALID_RECEIPT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReceiptEndpointTest {

  private static final Integer CASE_REF_TEST = 13;
  private static final String LOCALHOST = "http://localhost:";
  private static final String RECEIPT_PATH = "/questionnairereceipts";

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void shouldReturn204WhenSendingValidRequests() {
    Receipt inputReceipt = new Receipt();
    inputReceipt.setCaseRef(CASE_REF_TEST);

    ResponseEntity<?> entity = this.testRestTemplate.postForEntity(String.format("%s%d%s", LOCALHOST, port, RECEIPT_PATH),
            inputReceipt,
            ResponseEntity.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  public void shouldReturn400WhenSendingReceiptWithInvalidCaseRef() {
    Receipt inputReceipt = new Receipt();

    ResponseEntity<String> entity = this.testRestTemplate.postForEntity(String.format("%s%d%s", LOCALHOST, port, RECEIPT_PATH),
            inputReceipt,
            String.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    then(entity.getBody()).startsWith("{\"error\":{\"code\":\"VALIDATION_FAILED\",\"timestamp\":\"").endsWith(String.format("\",\"message\":\"%s\"}}", INVALID_RECEIPT));
  }
}
