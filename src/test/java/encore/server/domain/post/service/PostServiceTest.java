package encore.server.domain.post.service;

import encore.server.domain.hashtag.repository.HashtagRepository;
import encore.server.domain.hashtag.repository.PostHashtagRepository;
import encore.server.domain.post.converter.PostConverter;
import encore.server.domain.post.repository.PostImageRepository;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;
    @MockBean
    private PostImageRepository postImageRepository;
    @MockBean
    private PostConverter postConverter;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private HashtagRepository hashtagRepository;
    @MockBean
    private PostHashtagRepository postHashtagRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createPost() {
    }
}