package encore.server.domain.post.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

public record PostUpdateReq(
        @NotNull(message = "category is null")
        String category,
        @NotNull(message = "postType is null")
        String postType,
        @NotNull(message = "title is null")
        String title,
        @NotNull(message = "content is null")
        String content,
        List<String> imgUrls,
        List<String> hashTags,
        boolean isNotice, //null 로 전달될 경우 false
        boolean isTemporarySave
) {

    //compact 생성자
    public PostUpdateReq {
        // imgUrls와 hashTags가 null일 경우 빈 리스트로 초기화
        imgUrls = imgUrls == null ? Collections.emptyList() : List.copyOf(imgUrls);
        hashTags = hashTags == null ? Collections.emptyList() : List.copyOf(hashTags);
    }

}
