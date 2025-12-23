package encore.server.domain.image.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PreSignedUrlResponse {
  private String filePath;        // S3 파일 경로
  private String uploadUrl;  // Presigned PUT URL
  private String downloadUrl;
}
