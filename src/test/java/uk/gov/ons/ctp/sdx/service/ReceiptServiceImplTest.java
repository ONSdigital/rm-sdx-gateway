package uk.gov.ons.ctp.sdx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.sdx.service.impl.ReceiptServiceImpl.EXCEPTION_INVALID_RECEIPT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.sdx.quarantine.common.CTPException;
import uk.gov.ons.ctp.sdx.service.impl.ReceiptServiceImpl;

/** To unit test ReceiptServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ReceiptServiceImplTest {
  @Mock private CaseReceiptPublisher caseReceiptPublisher;

  @InjectMocks private ReceiptServiceImpl receiptService;

  public static final String CASE_REF = "abc";
  private static final String CASE_ID = "fa622b71-f158-4d51-82dd-c3417e31e32d";
  private static final String EMPTY_CASE_ID = "";

  /**
   * Tests valid receipt
   *
   * @throws CTPException ctpexception
   */
  @Test
  public void testValidReceipt() throws CTPException {
    Receipt receipt = new Receipt();
    receipt.setCaseRef(CASE_REF);
    receipt.setCaseId(CASE_ID);
    receipt.setInboundChannel(InboundChannel.ONLINE);

    receiptService.acknowledge(receipt);

    verify(caseReceiptPublisher, times(1)).send(any(CaseReceipt.class));
  }

  @Test
  public void testInvalidReceiptNullCaseId() {
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

    verify(caseReceiptPublisher, times(0)).send(any(CaseReceipt.class));
  }

  @Test
  public void testInvalidReceiptEmptyCaseId() {
    Receipt receipt = new Receipt();
    receipt.setCaseRef(CASE_REF);
    receipt.setCaseId(EMPTY_CASE_ID);

    boolean exceptionThrown = false;
    try {
      receiptService.acknowledge(receipt);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_INVALID_RECEIPT, e.getMessage());
    }
    assertTrue(exceptionThrown);

    verify(caseReceiptPublisher, times(0)).send(any(CaseReceipt.class));
  }
}
