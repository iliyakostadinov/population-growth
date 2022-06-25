package iliyakostadinov.populationgrowth.http;

import iliyakostadinov.populationgrowth.http.response.ErrorResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RequestErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = { IllegalArgumentException.class })
  protected ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex,
                                                    WebRequest request) {
    ErrorResponseBody errorResponseBody = new ErrorResponseBody(ex.getMessage());

    return handleExceptionInternal(ex,
                                   errorResponseBody,
                                   new HttpHeaders(),
                                   HttpStatus.BAD_REQUEST,
                                   request);
  }
}
