package encore.server.domain.image.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import encore.server.domain.image.dto.ImageGetPresignedUrlRequest;
import encore.server.domain.image.dto.ImageGetPresignedUrlResponse;
import encore.server.domain.image.dto.PreSignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  public PreSignedUrlResponse generatedUploadAndDownloadUrl(String prefix, String originalFileName) {
    // 1) S3 filePath 경로 생성
    String filePath = createS3Key(prefix, originalFileName);

    String uploadUrl = generateUploadUrl(filePath);

    String downloadUrl = generateGetPresignedUrl(filePath);

    return new PreSignedUrlResponse(filePath, uploadUrl, downloadUrl);
  }

  public ImageGetPresignedUrlResponse getDownloadPresignedUrl(ImageGetPresignedUrlRequest req) {
    return new ImageGetPresignedUrlResponse(generateGetPresignedUrl(req.filePath()));
  }

  public String generateGetPresignedUrl(String filePath) {
    GeneratePresignedUrlRequest request =
        new GeneratePresignedUrlRequest(bucket, filePath)
            .withMethod(HttpMethod.GET)
            .withExpiration(getExpiration());

    URL url = amazonS3.generatePresignedUrl(request);
    return url.toString();
  }

  /**
   * Presigned URL 반환 (업로드할 파일의 key 포함)
   *
   * @param s3FilePath        S3 에 저장될 FilePath
   * @return key + presigned URL
   */
  private String generateUploadUrl(String s3FilePath) {
    // 1) presigned request 생성
    GeneratePresignedUrlRequest request =
        createPresignedPutRequest(bucket, s3FilePath);

    // 2) URL 생성
    URL presignedUrl = amazonS3.generatePresignedUrl(request);

    return presignedUrl.toString();
  }

  /**
   * Presigned PUT URL 생성
   */
  private GeneratePresignedUrlRequest createPresignedPutRequest(String bucket, String filePath) {
    GeneratePresignedUrlRequest request =
        new GeneratePresignedUrlRequest(bucket, filePath)
            .withMethod(HttpMethod.PUT)
            .withExpiration(getExpiration());

    // PublicRead ACL 제거 (필요하다면 명시적으로 추가)
    // request.addRequestParameter("x-amz-acl", "public-read");

    return request;
  }

  /**
   * URL 유효 시간: 2분
   */
  private Date getExpiration() {
    Date expiration = new Date();
    expiration.setTime(expiration.getTime() + (1000 * 60 * 2));
    return expiration;
  }

  /**
   * S3 파일 저장 경로 생성 prefix/uuid.ext
   */
  private String createS3Key(String prefix, String originalFilename) {
    String uuid = UUID.randomUUID().toString();
    String extension = extractExtension(originalFilename);

    return prefix + "/" + uuid + extension;
  }

  /**
   * 확장자 추출 (.jpg, .png 등)
   */
  private String extractExtension(String filename) {
    int idx = filename.lastIndexOf(".");
    if (idx == -1) {
      return "";  // 확장자 없으면 빈 문자열
    }
    return filename.substring(idx);
  }
}