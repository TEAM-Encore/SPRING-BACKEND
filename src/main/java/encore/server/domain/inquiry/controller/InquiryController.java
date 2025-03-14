package encore.server.domain.inquiry.controller;

import encore.server.domain.inquiry.dto.CategoryAddRequest;
import encore.server.domain.inquiry.dto.ServiceUseRequest;
import encore.server.domain.inquiry.service.InquiryService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/inquiries")
@Tag(name = "Inquiry", description = "사용자 문의 API")
@Slf4j
public class InquiryController {

  private final InquiryService inquiryService;

  @PostMapping("/categories")
  @Operation(summary = "카테고리 추가 요청 API", description = "카테고리 추가 요청을 저장합니다.")
  public ApplicationResponse<Void> categoryAddRequest(
      @RequestBody @Valid CategoryAddRequest request,
      @LoginUserId Long userId) {
    inquiryService.saveCategoryAddRequest(request, userId);
    return ApplicationResponse.ok();
  }

  @PostMapping("/service-uses")
  @Operation(summary = "서비스 문의 API", description = "서비스 이용 문의를 저장합니다.")
  public ApplicationResponse<Void> serviceUseInquiry(
      @RequestBody @Valid ServiceUseRequest request,
      @LoginUserId Long userId) {
    inquiryService.saveServiceUseInquiry(request, userId);
    return ApplicationResponse.ok();
  }
}
