package encore.server.domain.hashtag.converter;

import encore.server.domain.hashtag.entity.PostHashtag;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostHashtagConverter {

    public List<String> stringListFrom(List<PostHashtag> postHashtags) {

        return postHashtags.stream()
                .map(ph -> ph.getHashtag().getName())
                .toList();

    }

}
