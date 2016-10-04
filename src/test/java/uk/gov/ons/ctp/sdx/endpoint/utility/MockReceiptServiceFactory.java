package uk.gov.ons.ctp.sdx.endpoint.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

public class MockReceiptServiceFactory implements Factory<ReceiptService> {

  public ReceiptService provide() {
    final ReceiptService mockedService = Mockito.mock(ReceiptService.class);
    return mockedService;
  }

  /**
   * dispose method
   *
   * @param t service to dispose
   */
  public void dispose(final ReceiptService t) {
  }
}