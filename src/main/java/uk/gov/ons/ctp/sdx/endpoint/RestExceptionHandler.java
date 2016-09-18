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

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {
  @ResponseBody
  @ExceptionHandler(CTPException.class)
  public ResponseEntity<?> handleCTPException(CTPException ex, Locale locale) {
    log.debug("Entering handleCTPException...");
    return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, Locale locale) {
    log.debug("Entering handleHttpMessageNotReadableException...");
    return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
  }
}
