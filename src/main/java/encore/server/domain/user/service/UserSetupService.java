package encore.server.domain.user.service;


import encore.server.domain.user.dto.request.UserPatchReq;
import encore.server.domain.user.dto.response.UserNicknameValidationRes;
import encore.server.domain.user.entity.PreferredKeyword;
import encore.server.domain.user.entity.TermOfUse;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.PreferredKeywordRepository;
import encore.server.domain.user.repository.TermOfUseRepository;
import encore.server.domain.user.repository.UserKeywordRepository;
import encore.server.domain.user.repository.UserRepository;
import encore.server.domain.user.repository.UserTermOfUseRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserSetupService {

  private final UserRepository userRepository;
  private final PreferredKeywordRepository preferredKeywordRepository;
  private final TermOfUseRepository termOfUseRepository;
  private final UserTermOfUseRepository userTermOfUseRepository;
  private final UserKeywordRepository userKeywordRepository;

  public UserNicknameValidationRes validateUserNickname(String nickname) {
    // business logic
    // 1. 허용 문자 검증
    if (!nickname.matches("^[가-힣a-zA-Z0-9]+$")) {
      throw new ApplicationException(ErrorCode.USER_NICKNAME_INVALID_FORMAT_EXCEPTION);
    }

    // 2. 길이 검증
    if (nickname.length() < 3) {
      throw new ApplicationException(ErrorCode.USER_NICKNAME_TOO_SHORT_EXCEPTION);
    }
    if (nickname.length() > 8) {
      throw new ApplicationException(ErrorCode.USER_NICKNAME_TOO_LONG_EXCEPTION);
    }

    // 3. 이미 존재하는 닉네임인지 검증
    if (userRepository.existsByNickName(nickname)) {
      throw new ApplicationException(ErrorCode.USER_NICKNAME_ALREADY_EXIST_EXCEPTION);
    }

    //return : 닉네임이 유효한지 반환
    return UserNicknameValidationRes.builder()
        .isValid(true)
        .build();
  }

  @Transactional
  public void patchUserInfo(UserPatchReq request, Long userId) {
    // validation : user 가 존재하는지 검증
    User userToUpdate = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    // business logic
    // 요청에 변경할 닉네임이 있을 경우 유효한 닉네임인지 검증 후 변경.
    if (request.nickName() != null) {
      validateUserNickname(request.nickName());
      userToUpdate.updateNickname(request.nickName());
    }
    // 요청에 프로필 이미지 URL 이 있을 경우 변경.
    if (request.profileImageUrl() != null) {
      userToUpdate.updateProfileImageUrl(request.profileImageUrl());
    }
    // 요청에 관람 빈도가 있을 경우 변경
    if (request.viewingFrequency() != null) {
      userToUpdate.updateViewingFrequency(request.viewingFrequency());
    }
    // 요청에 선호하는 공연 키워드가 있을 경우
    if (request.preferredKeywordEnums() != null) {
      // 요청으로 들어온 키워드를 DB 에서 가져옴
      List<PreferredKeyword> updateKeywordListManagedToEntityManager = preferredKeywordRepository.findAllByDeletedAtIsNullAndPreferredKeywordEnumIn(
          request.preferredKeywordEnums());
      // user 에 관련된 UserKeyword 중간 테이블 Row 를 모두 삭제
      userKeywordRepository.deleteAllByUserAndDeletedAtIsNull(userToUpdate);
      // 유저 키워드 연관관계 추가
      userToUpdate.addUserPreferredKeywords(updateKeywordListManagedToEntityManager);
    }
    // 요청에 이용약관이 있을 경우
    if (request.agreeTermEnums() != null) {
      // 요청으로 들어온 이용약관을 DB 에서 가져옴
      List<TermOfUse> updateTermOfUseListManagedToEntityManager = termOfUseRepository.findAllByDeletedAtIsNullAndTermTypeIn(
          request.agreeTermEnums());
      // user 에 관련되 UserTermOfUse 중간 테이블 Row 를 모두 삭제
      userTermOfUseRepository.deleteAllByUserAndDeletedAtIsNull(userToUpdate);
      // 이용약관 연관관계 추가
      userToUpdate.addUserTermOfUses(updateTermOfUseListManagedToEntityManager);
    }

  }


}
