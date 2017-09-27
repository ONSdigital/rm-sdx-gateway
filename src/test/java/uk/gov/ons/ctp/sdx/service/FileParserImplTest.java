package uk.gov.ons.ctp.sdx.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;

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
import static uk.gov.ons.ctp.sdx.service.impl.FileParserImpl.EXCEPTION_NO_RECORDS;

/**
 * To unit test FileParser
 */
@SpringBootTest(classes = FileParserImplTestConfig.class)
@RunWith(SpringRunner.class)
public class FileParserImplTest {

  private static final long THREE_SECONDS = 3000L;

  private static final String CASE_REF_1 = "0199917831739169";
  private static final String CASE_REF_2 = "0199917831739200";
  private static final String CASE_REF_3 = "0199917831739042";
  private static final String CASE_REF_DEFECT_1048 = "1000000000004714";

  private static final String CASE_RESPONSE_TIME_1 = "23/09/2016";
  private static final String CASE_RESPONSE_TIME_2 = "24/09/2016";
  private static final String CASE_RESPONSE_TIME_3 = "25/09/2016";
  private static final String CASE_RESPONSE_TIME_DEFECT_1048 = "12/01/2017";

  @Autowired
  private FileParser fileParser;

  @Value("${RESPONSE_DATE_TIME_COL_FORMAT}")
  private String responseDateTimeColFormat;

  /**
   * Tests a valid file
   * @throws CTPException ctpexception
   * @throws DatatypeConfigurationException datatypeconfigurationexception
   */
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
    exepectedResponseDateTimes.add(DateTimeUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_1,
        responseDateTimeColFormat));
    exepectedResponseDateTimes.add(DateTimeUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_2,
        responseDateTimeColFormat));
    exepectedResponseDateTimes.add(DateTimeUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_3,
        responseDateTimeColFormat));
    assertEquals(exepectedResponseDateTimes, responseDateTimes);
  }

  @Test
  public void testRandomFile() throws CTPException, DatatypeConfigurationException, ParseException {
    boolean exceptionThrown = false;
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/totalRandom.txt");
    try {
      fileParser.parseIt(inputStream);
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(EXCEPTION_NO_RECORDS, e.getMessage());
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testReceiptsWithInvalidDatesFile() throws CTPException, DatatypeConfigurationException {
    XMLGregorianCalendar now = DateTimeUtil.giveMeCalendarForNow();

    InputStream inputStream = getClass().getResourceAsStream(
        "/dailyPaperFiles/sampleReceiptsWithInvalidResponseTimes.csv");
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
      assertTrue(aCalendar.toGregorianCalendar().getTimeInMillis() -
          now.toGregorianCalendar().getTimeInMillis() < THREE_SECONDS);
    }
  }

  @Test
  public void testTwoValidReceiptsAndOneInvalidFile() throws CTPException, DatatypeConfigurationException {
    XMLGregorianCalendar now = DateTimeUtil.giveMeCalendarForNow();

    InputStream inputStream = getClass().getResourceAsStream(
        "/dailyPaperFiles/sampleTwoValidReceiptsOneInvalidReceiptMissingResponseTime.csv");
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
      if (aCalendar.compare(DateTimeUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_1,
          responseDateTimeColFormat)) == DatatypeConstants.EQUAL) {
        foundResponseTime1 = true;
      } else if (aCalendar.compare(DateTimeUtil.stringToXMLGregorianCalendar(CASE_RESPONSE_TIME_3,
          responseDateTimeColFormat)) == DatatypeConstants.EQUAL) {
        foundResponseTime3 = true;
      } else {
        assertTrue(aCalendar.toGregorianCalendar().getTimeInMillis() -
            now.toGregorianCalendar().getTimeInMillis() < THREE_SECONDS);
      }
    }
    assertTrue(foundResponseTime1);
    assertTrue(foundResponseTime3);
  }

  @Test
  public void testEmptyFile() throws CTPException, DatatypeConfigurationException, ParseException {
    boolean exceptionThrown = false;
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/emptyFile.csv");
    try {
      fileParser.parseIt(inputStream);
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(EXCEPTION_NO_RECORDS, e.getMessage());
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testDefect911File() throws CTPException, DatatypeConfigurationException, ParseException {
    boolean exceptionThrown = false;
    InputStream inputStream = getClass().getResourceAsStream("/dailyPaperFiles/defect911File.csv");
    try {
      fileParser.parseIt(inputStream);
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.VALIDATION_FAILED, e.getFault());
      assertEquals(EXCEPTION_NO_RECORDS, e.getMessage());
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }
}
