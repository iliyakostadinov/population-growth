package iliyakostadinov.populationgrowth.http.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class PopulationResponse<T> extends ResponseEntity<ResponseWrapper<T>> {
  public PopulationResponse(T body, HttpStatus status) {
    super(wrap(body), status);
  }

  public PopulationResponse(T body, MultiValueMap<String, String> headers, HttpStatus status) {
    super(wrap(body), headers, status);
  }

  public PopulationResponse(T body, MultiValueMap<String, String> headers, int rawStatus) {
    super(wrap(body), headers, rawStatus);
  }

  private static <U> ResponseWrapper<U> wrap(U body) {
    return body == null ? null : new ResponseWrapper<>(body);
  }
}
