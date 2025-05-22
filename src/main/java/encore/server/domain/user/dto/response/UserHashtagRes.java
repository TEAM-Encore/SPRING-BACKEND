package encore.server.domain.user.dto.response;

import encore.server.domain.hashtag.entity.UserHashtag;
import lombok.Builder;

import java.util.List;

@Builder
public record UserHashtagRes(
        Long id,
        String name
) {
    public static UserHashtagRes of(Long id, String name) {
        return UserHashtagRes.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static List<UserHashtagRes> listOf(List<UserHashtag> userHashtags) {
        return userHashtags.stream()
                .map(userHashtag -> UserHashtagRes.of(userHashtag.getId(), userHashtag.getHashtag().getName()))
                .toList();
    }
}
