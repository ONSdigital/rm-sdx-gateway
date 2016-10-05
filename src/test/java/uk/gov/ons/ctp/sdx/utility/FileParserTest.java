package uk.gov.ons.ctp.sdx.utility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseFeedback;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.ons.ctp.sdx.utility.FileParser.stringToXMLGregorianCalendar;

/**
 * To unit test FileParser
 */
@RunWith(MockitoJUnitRunner.class)
public class FileParserTest {

  @InjectMocks
  private FileParser fileParser;

  @Test
  public void testValidFile() throws CTPException, DatatypeConfigurationException, ParseException {
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
    exepectedResponseDateTimes.add(stringToXMLGregorianCalendar("2016-08-04T21:37:01.537Z"));
    exepectedResponseDateTimes.add(stringToXMLGregorianCalendar("2016-09-04T21:37:01.537Z"));
    exepectedResponseDateTimes.add(stringToXMLGregorianCalendar("2016-10-04T21:37:01.537Z"));
    assertEquals(exepectedResponseDateTimes, responseDateTimes);
  }
}
