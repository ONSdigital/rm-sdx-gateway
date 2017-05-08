package uk.gov.ons.ctp.sdx.endpoint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.sdx.service.ReceiptServiceImplTest.CASE_REF;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.sdx.BeanMapper;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

public class ReceiptEndpointUnitTest {

  private static final String LOCATION = "Location";
  private static final String RECEIPT_INVALIDJSON_SCENARIO1 = "{\"random\":  \"abc\"}";
  private static final String RECEIPT_INVALIDJSON_SCENARIO2 = "{\"caseRef\":  \"\"}";
  private static final String RECEIPT_VALIDJSON = String.format("{\"caseRef\":  \"%s\"}", CASE_REF);
  private static final String SERVER_URL = "/questionnairereceipts";
  /**
   * configure the test
   */
  @InjectMocks
  private ReceiptEndpoint receiptEndpoint;

  @Mock
  private ReceiptService receiptService;

  @Spy
  private MapperFacade mapperFacade = new BeanMapper();

  @LocalServerPort
  int runningPort;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
        .standaloneSetup(receiptEndpoint)
        .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
        .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
        .build();
  }

  @Test
  public void acknowledgeReceiptGoodJsonProvided() throws Exception {
    ResultActions actions = mockMvc.perform(postJson(SERVER_URL, RECEIPT_VALIDJSON));

    actions.andExpect(status().isCreated()).andExpect(jsonPath("$.caseRef",
        is(CASE_REF))).andExpect(header().string(LOCATION, "TODO"));

  }

  @Test
  public void acknowledgeReceiptBadJsonProvidedScenario1() throws Exception {
    ResultActions actions = mockMvc.perform(postJson(SERVER_URL, RECEIPT_INVALIDJSON_SCENARIO1));

    actions.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)))
        .andExpect(jsonPath("$.error.message", is("Provided json is incorrect.")));
  }

  @Test
  public void acknowledgeReceiptBadJsonProvidedScenario2() throws Exception {
    ResultActions actions = mockMvc.perform(postJson(SERVER_URL, RECEIPT_INVALIDJSON_SCENARIO2));

    actions.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)))
        .andExpect(jsonPath("$.error.message", is("Provided json fails validation.")));
  }
}
