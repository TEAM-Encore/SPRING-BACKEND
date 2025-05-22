package encore.server.domain.inquiry.service;

import encore.server.domain.inquiry.converter.InquiryConverter;
import encore.server.domain.inquiry.dto.CategoryAddRequest;
import encore.server.domain.inquiry.dto.ServiceUseRequest;
import encore.server.domain.inquiry.entity.InquiryAboutCategory;
import encore.server.domain.inquiry.entity.InquiryAboutServiceUse;
import encore.server.domain.inquiry.repository.InquiryAboutCategoryRepository;
import encore.server.domain.inquiry.repository.InquiryAboutServiceUseRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

  private final InquiryAboutCategoryRepository inquiryAboutCategoryRepository;
  private final InquiryAboutServiceUseRepository inquiryAboutServiceUseRepository;
  private final UserRepository userRepository;

  /**
   * 카테고리 추가 요청을 저장하는 메서드입니다.
   *
   * @param request 카테고리 추가 요청 정보를 담고 있는 DTO
   * @param userId  요청을 보낸 사용자의 ID
   */
  @Transactional
  public void saveCategoryAddRequest(CategoryAddRequest request, Long userId) {

    //business logic
    // userId에 해당하는 유저 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    // InquiryAboutCategory 엔티티 생성 (요청 데이터와 사용자 정보를 바탕으로 생성)
    InquiryAboutCategory inquiryAboutCategory = InquiryConverter.toInquiryAboutCategory(request,
        user);

    // 생성된 엔티티를 DB에 저장
    inquiryAboutCategoryRepository.save(inquiryAboutCategory);
  }

  /**
   * 서비스 이용 문의를 저장하는 메서드입니다.
   *
   * @param request 서비스 이용 문의 요청 정보를 담고 있는 DTO
   * @param userId 요청을 보낸 사용자의 ID
   */
  @Transactional
  public void saveServiceUseInquiry(ServiceUseRequest request, Long userId) {

    //business logic
    // userId에 해당하는 유저 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    // InquiryAboutServiceUse 엔티티 생성 (요청 데이터와 사용자 정보를 바탕으로 생성)
    InquiryAboutServiceUse inquiryAboutServiceUse = InquiryConverter.toInquiryAboutServiceUse(
        request, user);

    // 생성된 엔티티를 DB에 저장
    inquiryAboutServiceUseRepository.save(inquiryAboutServiceUse);
  }

}
