package encore.server.domain.user.service;

import encore.server.domain.user.entity.FCMToken;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.FcmTokenRepository;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFcmService {
    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public void addFCMToken(Long userId, String token) {
        // validation
        Optional<User> user = userRepository.findById(userId);

        // business logic
        FCMToken fcmToken = FCMToken.builder()
                .user(user.orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION)))
                .token(token)
                .build();
        fcmTokenRepository.save(fcmToken);
    }
}
