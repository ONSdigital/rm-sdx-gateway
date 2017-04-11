package uk.gov.ons.ctp.sdx.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.Publisher;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.sdx.message.CaseReceiptPublisher;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The publisher to queues
 */
@Slf4j
@Named
public class CaseReceiptPublisherImpl implements CaseReceiptPublisher {

  @Qualifier("caseReceiptRabbitTemplate")
  @Inject
  private RabbitTemplate rabbitTemplate;

  @Override
  public void send(CaseReceipt caseReceipt) {
    log.debug("send to queue caseReceipt {}", caseReceipt);
    rabbitTemplate.convertAndSend(caseReceipt);
  }
}
