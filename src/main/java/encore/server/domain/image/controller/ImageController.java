package encore.server.domain.image.controller;


import encore.server.domain.image.dto.ImageNameDTO;
import encore.server.domain.image.dto.PreSignedUrlResponse;
import encore.server.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
@Tag(name = "이미지 API", description = "Presigned URL 발급과 관련된 API입니다.")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned-url")
    @Operation(summary = "이미지 업로드용 presigned url 발급", description = "이미지 업로드용 presigned url을 발급합니다.")
    public ResponseEntity<PreSignedUrlResponse> saveImage(@RequestBody ImageNameDTO imageNameDto) {
        PreSignedUrlResponse res = imageService.generateUploadUrl("dynamic", imageNameDto.imageName());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/view/presigned-url")
    @Operation(summary = "이미지 조회용 presigned url 발급", description = "private 이미지 조회용 presigned GET URL을 발급합니다.")
    public ResponseEntity<String> viewImage(@RequestBody String filepath) {
        String url = imageService.generateGetPresignedUrl(filepath);
        return ResponseEntity.ok(url);
    }
}
