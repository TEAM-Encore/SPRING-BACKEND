package encore.server.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreSignedUrlResponse {
  private String filePath;        // S3 파일 경로
  private String uploadUrl;  // Presigned PUT URL
}
