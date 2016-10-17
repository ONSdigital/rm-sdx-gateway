package uk.gov.ons.ctp.sdx.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.time.DateUtil;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.sdx.service.impl.FileParserImpl;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
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

  private static final long THREE_SECONDS = 3000L;

  private static final String CASE_REF_1 = "123";
  private static final String CASE_REF_2 = "124";
  private static final String CASE_REF_3 = "125";

  private static final String CASE_RESPONSE_TIME_1 = "2016-08-04T21:37:01.537Z";
  private static final String CASE_RESPONSE_TIME_2 = "2016-09-04T21:37:01.537Z";
  private static final String CASE_RESPONSE_TIME_3 = "2016-10-04T21:37:01.537Z";

  @Autowired
  private FileParserImpl fileParser;

  @Value("${RESPONSE_DATE_TIME_COL_FORMAT}")
  private String responseDateTimeColFormat;

  @Test
  public void testValidFile() throws CTPException, DatatypeConfigurationException {
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/sampleAllThreeValidReceipts.csv");
    List<CaseReceipt> result = fileParser.parseIt(inputStream);

    assertNotNull(result);
    assertEquals(3, result.size());
    List<String> caseRefs = new ArrayList<>();
    List<XMLGregorianCalendar> responseDateTimes = new ArrayList<>();
    for (CaseReceipt caseReceipt: result) {
      assertEquals(InboundChannel.PAPER, caseReceipt.getInboundChannel());
      caseRefs.add(caseReceipt.getCaseRef());
      responseDateTimes.add(caseReceipt.getResponseDateTime());
    }

    List<String> expectedCaseRefs = new ArrayList<>();
    expectedCaseRefs.add(CASE_REF_1);
    expectedCaseRefs.add(CASE_REF_2);
    expectedCaseRefs.add(CASE_REF_3);
    assertEquals(expectedCaseRefs, caseRefs);

    List<XMLGregorianCalendar> exepectedResponseDateTimes = new ArrayList<>();
    exepectedResponseDateTimes.add(DateUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_1, responseDateTimeColFormat));
    exepectedResponseDateTimes.add(DateUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_2, responseDateTimeColFormat));
    exepectedResponseDateTimes.add(DateUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_3, responseDateTimeColFormat));
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
  public void testReceiptsWithInvalidDatesFile() throws CTPException, DatatypeConfigurationException {
    XMLGregorianCalendar now = DateUtil.giveMeCalendarForNow();

    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/sampleReceiptsWithInvalidResponseTimes.csv");
    List<CaseReceipt> result = fileParser.parseIt(inputStream);

    assertNotNull(result);
    assertEquals(3, result.size());
    List<String> caseRefs = new ArrayList<>();
    List<XMLGregorianCalendar> responseDateTimes = new ArrayList<>();
    for (CaseReceipt caseReceipt: result) {
      assertEquals(InboundChannel.PAPER, caseReceipt.getInboundChannel());
      caseRefs.add(caseReceipt.getCaseRef());
      responseDateTimes.add(caseReceipt.getResponseDateTime());
    }

    List<String> expectedCaseRefs = new ArrayList<>();
    expectedCaseRefs.add(CASE_REF_1);
    expectedCaseRefs.add(CASE_REF_2);
    expectedCaseRefs.add(CASE_REF_3);
    assertEquals(expectedCaseRefs, caseRefs);

    for (XMLGregorianCalendar aCalendar: responseDateTimes) {
      assertTrue(aCalendar.toGregorianCalendar().getTimeInMillis() - now.toGregorianCalendar().getTimeInMillis() < THREE_SECONDS);
    }
  }

  @Test
  public void testTwoValidReceiptsAndOneInvalidFile() throws CTPException, DatatypeConfigurationException {
    XMLGregorianCalendar now = DateUtil.giveMeCalendarForNow();

    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/sampleTwoValidReceiptsOneInvalidReceiptMissingResponseTime.csv");
    List<CaseReceipt> result = fileParser.parseIt(inputStream);

    assertNotNull(result);
    assertEquals(3, result.size());
    List<String> caseRefs = new ArrayList<>();
    List<XMLGregorianCalendar> responseDateTimes = new ArrayList<>();
    for (CaseReceipt caseReceipt: result) {
      assertEquals(InboundChannel.PAPER, caseReceipt.getInboundChannel());
      caseRefs.add(caseReceipt.getCaseRef());
      responseDateTimes.add(caseReceipt.getResponseDateTime());
    }

    List<String> expectedCaseRefs = new ArrayList<>();
    expectedCaseRefs.add(CASE_REF_1);
    expectedCaseRefs.add(CASE_REF_2);
    expectedCaseRefs.add(CASE_REF_3);
    assertEquals(expectedCaseRefs, caseRefs);

    boolean foundResponseTime1 = false; boolean foundResponseTime3 = false;
    for (XMLGregorianCalendar aCalendar: responseDateTimes) {
      if (aCalendar.compare(DateUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_1, responseDateTimeColFormat)) == DatatypeConstants.EQUAL) {
        foundResponseTime1 = true;
      } else if (aCalendar.compare(DateUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_3, responseDateTimeColFormat)) == DatatypeConstants.EQUAL) {
        foundResponseTime3 = true;
      } else {
        assertTrue(aCalendar.toGregorianCalendar().getTimeInMillis() - now.toGregorianCalendar().getTimeInMillis() < THREE_SECONDS);
      }
    }
    assertTrue(foundResponseTime1);
    assertTrue(foundResponseTime3);
  }

}
