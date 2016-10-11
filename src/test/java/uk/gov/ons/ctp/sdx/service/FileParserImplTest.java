package uk.gov.ons.ctp.sdx.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.service.impl.FileParserImpl;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.ons.ctp.common.error.CTPException.Fault.VALIDATION_FAILED;
import static uk.gov.ons.ctp.sdx.service.impl.FileParserImpl.EXCEPTION_NO_RECORDS;

/**
 * To unit test FileParser
 */
@SpringBootTest(classes = FileParserImplTestConfig.class)
@RunWith(SpringRunner.class)
public class FileParserImplTest {

  @Autowired
  private FileParserImpl fileParser;

  @Test
  public void testValidFile() throws CTPException, ParseException, DatatypeConfigurationException {
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/sampleAllThreeValidReceipts.csv");
    List<CaseFeedback> result = fileParser.parseIt(inputStream);

    assertNotNull(result);
    assertEquals(3, result.size());
    List<String> caseRefs = new ArrayList<>();
    List<XMLGregorianCalendar> responseDateTimes = new ArrayList<>();
    for (CaseFeedback caseFeedback: result) {
      assertEquals(InboundChannel.PAPER, caseFeedback.getInboundChannel());
      caseRefs.add(caseFeedback.getCaseRef());
      responseDateTimes.add(caseFeedback.getResponseDateTime());
    }

    List<String> expectedCaseRefs = new ArrayList<>();
    expectedCaseRefs.add("123");
    expectedCaseRefs.add("124");
    expectedCaseRefs.add("125");
    assertEquals(expectedCaseRefs, caseRefs);

    List<XMLGregorianCalendar> exepectedResponseDateTimes = new ArrayList<>();
    exepectedResponseDateTimes.add(fileParser.stringToXMLGregorianCalendar("2016-08-04T21:37:01.537Z"));
    exepectedResponseDateTimes.add(fileParser.stringToXMLGregorianCalendar("2016-09-04T21:37:01.537Z"));
    exepectedResponseDateTimes.add(fileParser.stringToXMLGregorianCalendar("2016-10-04T21:37:01.537Z"));
    assertEquals(exepectedResponseDateTimes, responseDateTimes);
  }

  @Test
  public void testRandomFile() throws CTPException, DatatypeConfigurationException, ParseException {
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/totalRandom.txt");
    boolean exceptionThrown = false;
    try {
      fileParser.parseIt(inputStream);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(VALIDATION_FAILED, e.getFault());
      assertEquals(EXCEPTION_NO_RECORDS, e.getMessage());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testAllInvalidReceiptsFile() throws CTPException {
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/sampleInvalidReceipts.csv");
    List<CaseFeedback> result = fileParser.parseIt(inputStream);
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testTwoValidReceiptsAndOneInvalidFile() throws CTPException, ParseException, DatatypeConfigurationException {
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/sampleTwoValidReceiptsOneInvalidReceiptMissingData.csv");
    List<CaseFeedback> result = fileParser.parseIt(inputStream);

    assertNotNull(result);
    assertEquals(2, result.size());
    List<String> caseRefs = new ArrayList<>();
    List<XMLGregorianCalendar> responseDateTimes = new ArrayList<>();
    for (CaseFeedback caseFeedback: result) {
      assertEquals(InboundChannel.PAPER, caseFeedback.getInboundChannel());
      caseRefs.add(caseFeedback.getCaseRef());
      responseDateTimes.add(caseFeedback.getResponseDateTime());
    }

    List<String> expectedCaseRefs = new ArrayList<>();
    expectedCaseRefs.add("123");
    expectedCaseRefs.add("125");
    assertEquals(expectedCaseRefs, caseRefs);

    List<XMLGregorianCalendar> exepectedResponseDateTimes = new ArrayList<>();
    exepectedResponseDateTimes.add(fileParser.stringToXMLGregorianCalendar("2016-08-04T21:37:01.537Z"));
    exepectedResponseDateTimes.add(fileParser.stringToXMLGregorianCalendar("2016-10-04T21:37:01.537Z"));
    assertEquals(exepectedResponseDateTimes, responseDateTimes);
  }

}
