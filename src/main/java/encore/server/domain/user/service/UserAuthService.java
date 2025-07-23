package encore.server.domain.user.service;

import encore.server.domain.user.converter.PenaltyHistoryConverter;
import encore.server.domain.user.converter.UserConverter;
import encore.server.domain.user.dto.request.UserImposePenaltyReq;
import encore.server.domain.user.dto.request.UserLoginReq;
import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.UserLoginRes;
import encore.server.domain.user.dto.response.UserPenaltyInfo;
import encore.server.domain.user.dto.response.UserSignupRes;
import encore.server.domain.user.entity.PenaltyHistory;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.enumerate.PenaltyStatus;
import encore.server.domain.user.repository.PenaltyHistoryRepository;
import encore.server.domain.user.repository.TermOfUseRepository;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.util.JwtUtils;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final TermOfUseRepository termOfUseRepository;
  private final PenaltyHistoryRepository penaltyHistoryRepository;
  private final Random random = new Random();

  @Transactional
  public UserSignupRes signup(UserSignupReq userSignupReq) {

    // validation: 회원가입된 유저인지 확인
    if (userRepository.existsByEmail(userSignupReq.email())) {
      throw new ApplicationException(ErrorCode.USER_ALREADY_EXIST_EXCEPTION);
    }

    // business logic: 비밀번호 인코딩 후 유저 저장
    String encodedPassword = passwordEncoder.encode(userSignupReq.password());

    // businessLogic: 요청값, 인코딩된 비밀번호, 랜덤 생성된 겹치지 않는 닉네임을 통해 User Entity를 만들고 저장함.
    String email = userRepository.save(
        UserConverter.toEntity(userSignupReq, encodedPassword, getUniqueNickName())).getEmail();

    // return: 유저 email 반환
    return UserSignupRes.builder()
        .email(email)
        .build();
  }

  public UserLoginRes login(UserLoginReq userLoginReq) {

    // validation: 회원가입된 유저인지 확인
    User findUser = userRepository.findByEmail(userLoginReq.email())
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    // validation: 비밀번호 확인
    if (!passwordEncoder.matches(userLoginReq.password(), findUser.getPassword())) {
      throw new ApplicationException(ErrorCode.PASSWORD_MISMATCH_EXCEPTION);
    }

    // businessLogic: AccessToken 생성
    String accessToken = jwtUtils.createToken(findUser.getEmail(), findUser.getId(), findUser.getRole());

    // businessLogic: 필수 이용 약관에 모두 동의하였는지 확인
    long numOfTermsByIsOptionalFalse =
        termOfUseRepository.countAllByDeletedAtIsNullAndIsOptionalFalse();

    long numOfAgreedRequiredTerm = findUser.getUserTermOfUses().stream()
        .filter(userTermOfUse -> Objects.isNull(userTermOfUse.getDeletedAt())
            && !userTermOfUse.getTermOfUse().getIsOptional())
        .count();


    // businessLogic: 정지당한 계정인지 확인
    Optional<PenaltyHistory> penalty = penaltyHistoryRepository.findByUserAndStatusAndDeletedAtIsNull(findUser, PenaltyStatus.ACTIVE);
    if (penalty.isPresent()) {
      // 정지내역이 존재한다면 내역 조회
      PenaltyHistory penaltyHistory = penalty.get();
      // 정지가 끝났을 경우 정지 해제
      if (penaltyHistory.getExpiresAt().isBefore(LocalDateTime.now())) {
        penaltyHistory.revokePenalty();
      }
      // 정지가 끝나지 않았을 경우 정지 내역과 함께 반환
      else {
        return UserLoginRes.builder()
            .accessToken(accessToken)
            .isAgreedRequiredTerm(numOfAgreedRequiredTerm == numOfTermsByIsOptionalFalse)
            .isActivePenalty(true)
            .userPenaltyInfo(
                UserPenaltyInfo.builder()
                    .penaltyStatus(penaltyHistory.getStatus())
                    .penaltyType(penaltyHistory.getType())
                    .reason(penaltyHistory.getReason())
                    .issuedAt(penaltyHistory.getIssuedAt())
                    .expiresAt(penaltyHistory.getExpiresAt())
                    .build()
            )
            .build();
      }
    }

    return UserLoginRes.builder()
        .accessToken(accessToken)
        .isAgreedRequiredTerm(numOfAgreedRequiredTerm == numOfTermsByIsOptionalFalse)
        .isActivePenalty(false)
        .build();
  }

  public String getUniqueNickName() {

    StringBuilder uniqueNickNameStringBuilder = new StringBuilder();
    String uniqueNickName;

    while (true) {
      uniqueNickNameStringBuilder
          .append("뮤사랑")
          .append(random.nextInt(10))
          .append(random.nextInt(10))
          .append(random.nextInt(10))
          .append(random.nextInt(10))
          .append(random.nextInt(10));

      uniqueNickName = uniqueNickNameStringBuilder.toString();

      if (!userRepository.existsByNickName(uniqueNickName)) {
        break;
      }

      uniqueNickNameStringBuilder.setLength(0);
    }

    return uniqueNickName;
  }

  @Transactional
  public void imposePenalty(UserImposePenaltyReq penalty) {
    //validation: 존재하는 유저인지 확인
    User user = userRepository.findByIdAndDeletedAtIsNull(penalty.userId())
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
    //business logic: 만료 시간 설정
    LocalDateTime expiresAt = switch (penalty.type()) {
      case ONE_WEEK -> LocalDateTime.now().plusWeeks(1);
      case TWO_WEEK -> LocalDateTime.now().plusWeeks(2);
      case ONE_MONTH -> LocalDateTime.now().plusMonths(1);
      case PERM_BAN -> LocalDateTime.MAX;
    };
    //계정 정지 현황 저장
    penaltyHistoryRepository.save(PenaltyHistoryConverter.toEntity(penalty, user, expiresAt));
  }
}
