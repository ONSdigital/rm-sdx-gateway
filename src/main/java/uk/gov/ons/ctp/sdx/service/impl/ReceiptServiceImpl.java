package uk.gov.ons.ctp.sdx.service.impl;

import java.io.InputStream;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.sdx.service.FileParser;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

/**
 * The service to acknowlegde receipts
 */
@Slf4j
@Component
public class ReceiptServiceImpl implements ReceiptService {

  private static final String EXCEPTION_ACKNOWLEGDING_RECEIPT =
          "An unexpected error occured while acknowledging your receipt. ";
  public static final String EXCEPTION_INVALID_RECEIPT = "Invalid receipt. It can't be acknowledged.";

  @Autowired
  private CaseReceiptPublisher caseReceiptPublisher;

  @Autowired
  private FileParser fileParser;

  @Override
  public final void acknowledge(Receipt receipt) throws CTPException {
    log.debug("acknowledging receipt {}", receipt);
    validate(receipt);

    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(receipt.getCaseRef().trim());
    caseReceipt.setInboundChannel(receipt.getInboundChannel());
    try {
      caseReceipt.setResponseDateTime(DateTimeUtil.giveMeCalendarForNow());
    } catch (DatatypeConfigurationException e) {
      String error = String.format(
              "DatatypeConfigurationException thrown while building dateTime for now with msg = %s", e.getMessage());
      log.error(error);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR,
              String.format("%s%s", EXCEPTION_ACKNOWLEGDING_RECEIPT, error));
    }

    caseReceiptPublisher.send(caseReceipt);
  }

  @Override
  public void acknowledgeFile(InputStream fileContents) throws CTPException {
    log.debug("acknowledgeFile {}", fileContents);
    List<CaseReceipt> caseReceipts = fileParser.parseIt(fileContents);
    caseReceipts.forEach(caseReceipt -> caseReceiptPublisher.send(caseReceipt));
  }

  /**
   * To validate a receipt
   * @param receipt to be validated
   * @throws CTPException if the receipt does NOT have an inboundChannel.
   */
  private static void validate(Receipt receipt) throws CTPException {
    InboundChannel inboundChannel = receipt.getInboundChannel();
    if (inboundChannel == null) {
      log.error(EXCEPTION_INVALID_RECEIPT);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_INVALID_RECEIPT);
    }
  }



}
