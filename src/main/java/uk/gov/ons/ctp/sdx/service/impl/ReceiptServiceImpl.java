package uk.gov.ons.ctp.sdx.service.impl;

import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.sdx.quarantine.common.CTPException;
import uk.gov.ons.ctp.sdx.service.ReceiptService;

/** The service to acknowlegde receipts */
@Slf4j
@Service
public class ReceiptServiceImpl implements ReceiptService {

  public static final String EXCEPTION_INVALID_RECEIPT =
      "Invalid receipt. It can't be acknowledged.";

  @Autowired private CaseReceiptPublisher caseReceiptPublisher;

  @Override
  public final void acknowledge(Receipt receipt) throws CTPException {
    log.debug("acknowledging receipt {}", receipt);
    validate(receipt);

    CaseReceipt caseReceipt = new CaseReceipt();
    String caseRef = receipt.getCaseRef();
    if (caseRef != null) {
      caseReceipt.setCaseRef(caseRef.trim());
    }
    caseReceipt.setCaseId((receipt.getCaseId()));
    caseReceipt.setPartyId(receipt.getUserId());
    caseReceipt.setInboundChannel(receipt.getInboundChannel());

    caseReceiptPublisher.send(caseReceipt);
  }

  @Override
  public void acknowledgeFile(InputStream fileContents) {
    log.debug("acknowledgeFile {}", fileContents);
    throw new UnsupportedOperationException("File acknowledgement no longer supported");
  }

  /**
   * To validate a receipt
   *
   * @param receipt to be validated
   * @throws CTPException if the receipt does NOT have an inboundChannel.
   */
  private static void validate(Receipt receipt) throws CTPException {
    String caseId = receipt.getCaseId();
    InboundChannel inboundChannel = receipt.getInboundChannel();
    if (StringUtils.isEmpty(caseId) || inboundChannel == null) {
      log.error(EXCEPTION_INVALID_RECEIPT);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_INVALID_RECEIPT);
    }
  }
}
