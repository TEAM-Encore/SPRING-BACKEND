package encore.server.domain.user.dto.response;

import lombok.Builder;

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
}
