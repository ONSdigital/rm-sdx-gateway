package uk.gov.ons.ctp.sdx.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseFeedbackPublisher;
import uk.gov.ons.ctp.sdx.service.impl.ReceiptServiceImpl;
import uk.gov.ons.ctp.sdx.utility.FileParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.sdx.service.impl.ReceiptServiceImpl.EXCEPTION_INVALID_RECEIPT;

/**
 * To unit test ReceiptServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ReceiptServiceImplTest {
  @Mock
  CaseFeedbackPublisher caseFeedbackPublisher;

  @Mock
  FileParser fileParser;

  @InjectMocks
  ReceiptServiceImpl receiptService;

  public static final String CASE_REF = "abc";

  @Test
  public void testValidReceipt() throws CTPException {
    Receipt receipt = new Receipt();
    receipt.setCaseRef(CASE_REF);
    receipt.setInboundChannel(InboundChannel.ONLINE);

    receiptService.acknowledge(receipt);

    verify(caseFeedbackPublisher, times(1)).send(any(CaseFeedback.class));
  }

  @Test
  public void testInvalidReceipt() {
    Receipt receipt = new Receipt();
    receipt.setCaseRef(CASE_REF);

    boolean exceptionThrown = false;
    try {
      receiptService.acknowledge(receipt);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_INVALID_RECEIPT, e.getMessage());
    }
    assertTrue(exceptionThrown);

    verify(caseFeedbackPublisher, times(0)).send(any(CaseFeedback.class));
  }

  @Test
  public void testValidFileReceipt() throws CTPException{
    List<CaseFeedback> caseFeedbacks = new ArrayList<>();
    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedbacks.add(caseFeedback);
    when(fileParser.parseIt(any(InputStream.class))).thenReturn(caseFeedbacks);

    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/sampleAllThreeValidReceipts.csv");
    receiptService.acknowledgeFile(inputStream);

    verify(caseFeedbackPublisher, times(1)).send(any(CaseFeedback.class));
  }
}
