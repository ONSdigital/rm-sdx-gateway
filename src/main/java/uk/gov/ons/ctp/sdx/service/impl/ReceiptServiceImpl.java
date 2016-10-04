package uk.gov.ons.ctp.sdx.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseFeedbackPublisher;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

@Slf4j
@Named
public class ReceiptServiceImpl implements ReceiptService {

  private static final String EXCEPTION_ACKNOWLEGDING_RECEIPT =
          "An unexpected error occured while acknowledging your receipt. ";
  @Inject
  private CaseFeedbackPublisher caseFeedbackPublisher;

  @Override
  public void acknowledge(Receipt receipt) throws CTPException {
    log.debug("acknowledging receipt {}", receipt);
    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedback.setCaseRef(receipt.getCaseRef());
    caseFeedback.setResponseDateTime(giveMeCalendarForNow());
    // TODO Remove hardcoding below
    caseFeedback.setInboundChannel("online");
    caseFeedbackPublisher.send(caseFeedback);
  }

  public static XMLGregorianCalendar giveMeCalendarForNow() throws CTPException {
    java.util.GregorianCalendar gregorianCalendar = new java.util.GregorianCalendar();
    gregorianCalendar.setTime(new Date());

    javax.xml.datatype.XMLGregorianCalendar result = null;
    try {
      javax.xml.datatype.DatatypeFactory factory = javax.xml.datatype.DatatypeFactory.newInstance();
      result = factory.newXMLGregorianCalendar(
              gregorianCalendar.get(java.util.GregorianCalendar.YEAR),
              gregorianCalendar.get(java.util.GregorianCalendar.MONTH) + 1,
              gregorianCalendar.get(java.util.GregorianCalendar.DAY_OF_MONTH),
              gregorianCalendar.get(java.util.GregorianCalendar.HOUR_OF_DAY),
              gregorianCalendar.get(java.util.GregorianCalendar.MINUTE),
              gregorianCalendar.get(java.util.GregorianCalendar.SECOND),
              gregorianCalendar.get(java.util.GregorianCalendar.MILLISECOND), 0);
    } catch (DatatypeConfigurationException e) {
      String error = String.format(
              "DatatypeConfigurationException thrown while building dateTime for now with msg = %s", e.getMessage());
      log.error(error);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR,
              String.format("%s%s", EXCEPTION_ACKNOWLEGDING_RECEIPT, error));
    }
    return result;
  }
}
