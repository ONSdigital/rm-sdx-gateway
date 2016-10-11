package uk.gov.ons.ctp.sdx.utility;

import uk.gov.ons.ctp.common.error.CTPException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

/**
 * A class to combine all utils required on dates, XMLGregorianCalendar, etc.
 */
public class DateUtils {
  /**
   * To get a XMLGregorianCalendar for now
   * @return a XMLGregorianCalendar for now
   * @throws CTPException if it can't create a calendar
   */
  public static XMLGregorianCalendar giveMeCalendarForNow() throws DatatypeConfigurationException {
    java.util.GregorianCalendar gregorianCalendar = new java.util.GregorianCalendar();
    gregorianCalendar.setTime(new Date());

    javax.xml.datatype.XMLGregorianCalendar result = null;
    javax.xml.datatype.DatatypeFactory factory = javax.xml.datatype.DatatypeFactory.newInstance();
    result = factory.newXMLGregorianCalendar(
            gregorianCalendar.get(java.util.GregorianCalendar.YEAR),
            gregorianCalendar.get(java.util.GregorianCalendar.MONTH) + 1,
            gregorianCalendar.get(java.util.GregorianCalendar.DAY_OF_MONTH),
            gregorianCalendar.get(java.util.GregorianCalendar.HOUR_OF_DAY),
            gregorianCalendar.get(java.util.GregorianCalendar.MINUTE),
            gregorianCalendar.get(java.util.GregorianCalendar.SECOND),
            gregorianCalendar.get(java.util.GregorianCalendar.MILLISECOND), 0);
    return result;
  }
}
