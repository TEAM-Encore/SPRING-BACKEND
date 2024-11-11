package encore.server.domain.post.converter;


import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class PostConverter {

    //PostCreateReq -> Post 로 변환
    public Post convert(PostCreateReq postCreateReq, User user){
        return new Post(user,
                postCreateReq.title(),
                postCreateReq.content(),
                postCreateReq.isNotice(),
                postCreateReq.isTemporarySave(),
                PostType.valueOf(postCreateReq.postType()),
                Category.valueOf(postCreateReq.category()),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }


}
