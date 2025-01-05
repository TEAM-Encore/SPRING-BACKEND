package encore.server.domain.user.service;


import encore.server.domain.user.dto.response.UserNicknameValidationRes;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserSetupService {

  private final UserRepository userRepository;

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

}
