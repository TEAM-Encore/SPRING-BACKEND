package encore.server.domain.user.service;

import encore.server.domain.hashtag.entity.Hashtag;
import encore.server.domain.hashtag.entity.UserHashtag;
import encore.server.domain.hashtag.repository.HashtagRepository;
import encore.server.domain.hashtag.repository.UserHashtagRepository;
import encore.server.domain.user.dto.response.UserHashtagRes;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserHashtagService {
    private final UserRepository userRepository;
    private final UserHashtagRepository userHashtagRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional
    public UserHashtagRes addHashtag(Long userId, String name) {
        // validation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if(userHashtagRepository.countByUser(user) >= 10) {
            throw new ApplicationException(ErrorCode.USER_HASHTAG_LIMIT_EXCEEDED_EXCEPTION);
        }

        // business logic
        // 해당 유저가 같은 해시태그를 이미 추가했는지 확인
        userHashtagRepository.findByUserAndHashtagName(user, name)
                .ifPresent(existing -> {
                    throw new ApplicationException(ErrorCode.USER_HASHTAG_ALREADY_EXISTS_EXCEPTION);
                });

        // 해시태그가 DB에 존재하는지 확인
        Hashtag hashtag = hashtagRepository.findByName(name)
                .orElseGet(() -> {
                    Hashtag newHashtag = Hashtag.builder().name(name).build();
                    return hashtagRepository.save(newHashtag);
                });

        // UserHashtag 저장
        UserHashtag userHashtag = UserHashtag.builder()
                .user(user)
                .hashtag(hashtag)
                .build();

        userHashtagRepository.save(userHashtag);

        // response
        return UserHashtagRes.builder()
                .id(userHashtag.getId())
                .name(userHashtag.getHashtag().getName())
                .build();
    }

    @Transactional
    public void deleteHashtag(Long userId, Long id) {
        // validation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        UserHashtag userHashtag = userHashtagRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_HASHTAG_NOT_FOUND_EXCEPTION));

        // business logic
        userHashtagRepository.delete(userHashtag);
    }

    public List<UserHashtagRes> getHashtags(Long userId) {
        // validation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // business logic
        List<UserHashtag> userHashtags = userHashtagRepository.findAllByUser(user);

        // response
        return UserHashtagRes.listOf(userHashtags);
    }
}
