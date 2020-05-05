package uk.gov.ons.ctp.sdx.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import uk.gov.ons.ctp.sdx.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.sdx.quarantine.casesvc.CaseReceipt;

/** The publisher to queues */
@Slf4j
@MessageEndpoint
public class CaseReceiptPublisherImpl implements CaseReceiptPublisher {

  @Qualifier("caseReceiptRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  public void send(CaseReceipt caseReceipt) {
    log.debug("send to queue caseReceipt {}", caseReceipt);
    rabbitTemplate.convertAndSend(caseReceipt);
  }
}
