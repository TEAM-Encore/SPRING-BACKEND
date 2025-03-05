package encore.server.domain.user.service;

import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.hashtag.entity.UserHashtag;
import encore.server.domain.hashtag.repository.UserHashtagRepository;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.service.PostService;
import encore.server.domain.user.entity.FCMToken;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.FcmTokenRepository;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.config.FCMConfig;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFcmService {
    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserHashtagRepository userHashtagRepository;
    private final FCMConfig fcmConfig;
    private final PostService postService;

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

    public void notifyUsersByHashtag(Post post, List<PostHashtag> postHashtags){
        // 게시글 해시태그 리스트 추출
        List<String> postHashtagNames = postHashtags.stream()
                .map(ph -> ph.getHashtag().getName())
                .collect(Collectors.toList());

        // 해당 해시태그를 설정한 사용자 찾기
        List<UserHashtag> matchedUserHashtags = userHashtagRepository.findByHashtagNameIn(postHashtagNames);

        // 알림 보낼 사용자 목록
        Set<User> usersToNotify = matchedUserHashtags.stream()
                .map(UserHashtag::getUser)
                .collect(Collectors.toSet());

        // FCM을 통해 알림 전송
        for (User user : usersToNotify) {
            fcmConfig.sendByTokenList(
                    user.getFcmTokens().stream()
                            .map(FCMToken::getToken)
                            .collect(Collectors.toList()),
                    post.getTitle(),
                    post.getUser().getName());
        }
    }
}
