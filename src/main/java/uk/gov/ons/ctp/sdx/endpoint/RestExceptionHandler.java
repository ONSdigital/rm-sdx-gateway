package uk.gov.ons.ctp.sdx.endpoint;

import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.ons.ctp.sdx.error.CTPException;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {
  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, Locale locale) {
    log.debug("Entering handleMethodArgumentNotValidException...");
    CTPException ctpException = new CTPException(CTPException.Fault.VALIDATION_FAILED, ex.getMessage());
    return new ResponseEntity<>(ctpException, HttpStatus.BAD_REQUEST);
  }
}
