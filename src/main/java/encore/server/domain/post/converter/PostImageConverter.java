package encore.server.domain.post.converter;

import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.post.entity.PostImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostImageConverter {

    public List<String> stringListFrom(List<PostImage> postImages) {

        return postImages.stream()
                .map(ph -> ph.getUrl())
                .toList();

    }

}
