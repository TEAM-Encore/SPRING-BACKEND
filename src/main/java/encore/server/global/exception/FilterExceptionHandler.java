package encore.server.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;

public class FilterExceptionHandler {
  public static ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  public static void handle(ApplicationException e, HttpServletResponse response) {
    response.setStatus(e.getErrorCode().getHttpStatus().value()); // 보통 401/403
    response.setContentType("application/json;charset=UTF-8");
    try {
      String body = objectMapper.writeValueAsString(new ErrorResponse(e.getErrorCode()));
      response.getWriter().write(body);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
