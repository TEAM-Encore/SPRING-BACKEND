package encore.server.domain.image.controller;


import encore.server.domain.image.dto.ImageNameDTO;
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
    public ResponseEntity<String> saveImage(@RequestBody ImageNameDTO imageNameDto) {
        String preSignedUrl = imageService.getPreSignedUrl("encore", imageNameDto.imageName());
        return ResponseEntity.ok(preSignedUrl);
    }
}