package uk.gov.ons.ctp.sdx.message;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.utility.CaseBoundMessageListener;
import uk.gov.ons.ctp.sdx.utility.JmsHelper;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.ons.ctp.common.time.DateUtil.giveMeCalendarForNow;
import static uk.gov.ons.ctp.sdx.service.ReceiptServiceImplTest.CASE_REF;

/**
 * Test focusing on Spring Integration
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseReceiptPublisherImplITCaseConfig.class)
public class CaseReceiptPublisherImplITCase {

  @Autowired
  CaseReceiptPublisher caseReceiptPublisher;

  @Autowired
  DefaultMessageListenerContainer caseReceiptMessageListenerContainer;

  @Autowired
  CachingConnectionFactory connectionFactory;

  private Connection connection;
  private int initialCounter;

  private static final String INVALID_CASE_RECEIPTS_QUEUE = "Case.InvalidCaseReceipts";

  @Before
  public void setUp() throws JMSException {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);

    CaseBoundMessageListener listener = (CaseBoundMessageListener) caseReceiptMessageListenerContainer.getMessageListener();
    listener.setPayload(null);
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  /**
   * This test sends a valid CaseReceipt using CaseReceiptPublisher. It then verifies that the correct message is
   * received on the queue Case.Responses. See the definition of the jmsContainer in test-outbound-only-int.xml
   */
  @Test
  public void testSendValidCaseReceiptWithCaseReceiptPublisher() throws Exception {
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseRef(CASE_REF);
    caseReceipt.setInboundChannel(InboundChannel.ONLINE);
    caseReceipt.setResponseDateTime(giveMeCalendarForNow());
    caseReceiptPublisher.send(caseReceipt);

    Thread.sleep(10000L);

    /**
     * We check that no additional message has been put on the xml invalid queue
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * The section below verifies that a CaseReceipt ends up on the queue
     */
    CaseBoundMessageListener listener = (CaseBoundMessageListener) caseReceiptMessageListenerContainer.getMessageListener();
    TimeUnit.SECONDS.sleep(10);
    String listenerPayload = listener.getPayload();

    // TODO Unfortunately, when building via the command line, the test may fail with a nullpointer.
    //if (listenerPayload != null) {
    JAXBContext jaxbContext = JAXBContext.newInstance(CaseReceipt.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    CaseReceipt retrievedCaseReceipt = (CaseReceipt) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(listenerPayload.getBytes()));
    assertEquals(caseReceipt.getCaseRef(), retrievedCaseReceipt.getCaseRef());
    assertEquals(caseReceipt.getInboundChannel(), retrievedCaseReceipt.getInboundChannel());
    assertEquals(caseReceipt.getResponseDateTime(), retrievedCaseReceipt.getResponseDateTime());
    //}
  }

  @Test
  public void testSendInvalidCaseReceiptWithCaseReceiptPublisher() throws Exception {
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceiptPublisher.send(caseReceipt);

    Thread.sleep(10000L);

    /**
     * We check that the xml invalid queue contains 1 additional message.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(1, finalCounter - initialCounter);

    /**
     * The section below verifies that no CaseReceipt ends up on the queue
     */
    CaseBoundMessageListener listener = (CaseBoundMessageListener) caseReceiptMessageListenerContainer.getMessageListener();
    TimeUnit.SECONDS.sleep(10);
    String listenerPayload = listener.getPayload();
    assertNull(listenerPayload);
  }

}
