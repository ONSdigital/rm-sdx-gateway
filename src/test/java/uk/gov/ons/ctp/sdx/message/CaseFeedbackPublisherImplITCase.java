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
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
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
import static uk.gov.ons.ctp.sdx.service.ReceiptServiceImplTest.CASE_REF;
import static uk.gov.ons.ctp.sdx.service.impl.ReceiptServiceImpl.giveMeCalendarForNow;

/**
 * Test focusing on Spring Integration
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseFeedbackPublisherImplITCaseConfig.class)
public class CaseFeedbackPublisherImplITCase {

  @Autowired
  CaseFeedbackPublisher caseFeedbackPublisher;

  @Autowired
  DefaultMessageListenerContainer caseFeedbackMessageListenerContainer;

  @Autowired
  CachingConnectionFactory connectionFactory;

  private Connection connection;
  private int initialCounter;

  private static final String INVALID_CASE_FEEDBACKS_QUEUE = "Case.InvalidCaseFeedbacks";

  @Before
  public void setUp() throws JMSException {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_FEEDBACKS_QUEUE);

    CaseBoundMessageListener listener = (CaseBoundMessageListener)caseFeedbackMessageListenerContainer.getMessageListener();
    listener.setPayload(null);
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  /**
   * This test sends a valid CaseFeedback using CaseFeedbackPublisher. It then verifies that the correct message is
   * received on the queue Case.Responses. See the definition of the jmsContainer in test-outbound-only-int.xml
   */
  @Test
  public void testSendValidCaseFeedbackWithCaseFeedbackPublisher() throws Exception {
    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedback.setCaseRef(CASE_REF);
    caseFeedback.setInboundChannel(InboundChannel.ONLINE);
    caseFeedback.setResponseDateTime(giveMeCalendarForNow());
    caseFeedbackPublisher.send(caseFeedback);

    Thread.sleep(10000L);

    /**
     * We check that no additional message has been put on the xml invalid queue
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_FEEDBACKS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * The section below verifies that a CaseFeedback ends up on the queue
     */
    CaseBoundMessageListener listener = (CaseBoundMessageListener)caseFeedbackMessageListenerContainer.getMessageListener();
    TimeUnit.SECONDS.sleep(10);
    String listenerPayload = listener.getPayload();

    // TODO Unfortunately, when building via the command line, the test may fail with a nullpointer.
    //if (listenerPayload != null) {
    JAXBContext jaxbContext = JAXBContext.newInstance(CaseFeedback.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    CaseFeedback retrievedCaseFeedback = (CaseFeedback) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(listenerPayload.getBytes()));
    assertEquals(caseFeedback.getCaseRef(), retrievedCaseFeedback.getCaseRef());
    assertEquals(caseFeedback.getInboundChannel(), retrievedCaseFeedback.getInboundChannel());
    assertEquals(caseFeedback.getResponseDateTime(), retrievedCaseFeedback.getResponseDateTime());
    //}
  }

  @Test
  public void testSendInvalidCaseFeedbackWithCaseFeedbackPublisher() throws Exception {
    // Note the missing InboundChannel
    CaseFeedback caseFeedback = new CaseFeedback();
    caseFeedback.setCaseRef(CASE_REF);
    caseFeedback.setResponseDateTime(giveMeCalendarForNow());
    caseFeedbackPublisher.send(caseFeedback);

    Thread.sleep(10000L);

    /**
     * We check that the xml invalid queue contains 1 additional message.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_FEEDBACKS_QUEUE);
    assertEquals(1, finalCounter - initialCounter);

    /**
     * The section below verifies that no CaseFeedback ends up on the queue
     */
    CaseBoundMessageListener listener = (CaseBoundMessageListener)caseFeedbackMessageListenerContainer.getMessageListener();
    TimeUnit.SECONDS.sleep(10);
    String listenerPayload = listener.getPayload();
    assertNull(listenerPayload);
  }

}
