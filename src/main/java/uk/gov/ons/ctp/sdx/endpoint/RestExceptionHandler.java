package uk.gov.ons.ctp.sdx.endpoint;

import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.ons.ctp.sdx.error.CTPException;

import static uk.gov.ons.ctp.sdx.endpoint.ReceiptEndpoint.INVALID_RECEIPT;

/**
 * The handler for exceptions
 */
@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

  /**
   * To intercept {@link CTPException}
   * @param ctpException the exception orignally thrown
   * @param locale the locale
   * @return the relevant ResponseEntity
   */
  @ResponseBody
  @ExceptionHandler(CTPException.class)
  public ResponseEntity<?> handleCTPException(CTPException ctpException, Locale locale) {
    log.debug("Entering handleCTPException...");
    HttpStatus returnedStatus;
    switch(ctpException.getFault()){
      case VALIDATION_FAILED:
        returnedStatus = HttpStatus.BAD_REQUEST;
        break;
      case RESOURCE_NOT_FOUND:
        returnedStatus = HttpStatus.NOT_FOUND;
        break;
      case RESOURCE_VERSION_CONFLICT:
        returnedStatus = HttpStatus.CONFLICT;
        break;
      case ACCESS_DENIED:
        returnedStatus = HttpStatus.FORBIDDEN;
        break;
      default:
        returnedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return new ResponseEntity<>(ctpException, returnedStatus);
  }

  /**
   * To intercept {@link HttpMessageNotReadableException}
   * @param ex the exception originally thrown
   * @param locale the locale
   * @return the relevant ResponseEntity
   */
  @ResponseBody
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, Locale locale) {
    log.debug("Entering handleHttpMessageNotReadableException...");
    CTPException ctpException = new CTPException(CTPException.Fault.VALIDATION_FAILED, INVALID_RECEIPT);
    return new ResponseEntity<>(ctpException, HttpStatus.BAD_REQUEST);
  }
}
