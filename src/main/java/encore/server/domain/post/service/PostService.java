package encore.server.domain.post.service;

import encore.server.domain.hashtag.entity.Hashtag;
import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.hashtag.repository.HashtagRepository;
import encore.server.domain.hashtag.repository.PostHashtagRepository;
import encore.server.domain.hashtag.service.HashtagService;
import encore.server.domain.post.converter.PostConverter;
import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostImage;
import encore.server.domain.post.repository.PostImageRepository;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostConverter postConverter;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;


    @Transactional
    public Long createPost(PostCreateReq postCreateReq, Long userId){

        log.info("[POST]-[PostService]-[createPost] method call");

        //1. Convert DTO to Entity
        User userFindById = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Post post = postConverter.convert(postCreateReq, userFindById);
        List<String> imgUrls = postCreateReq.imgUrls();
        List<String> hashTags = postCreateReq.hashTags();

        //2. Post Entity Save
        Post savedPost = postRepository.save(post);
        log.info("[POST]-[PostService]-[createPost] Post save success");

        //3. Image URL processing
        // 이미지 url추출 후 따로 PostImage 저장(post pk를 sequence로 하는게 성능상 좋을수도)
        List<PostImage> imagesToSave = new ArrayList<>();

        for(String imgUrl : imgUrls){
            PostImage imageToSave = new PostImage(null, imgUrl, savedPost); //id는 왜 있지
            imagesToSave.add(imageToSave);
        }

        postImageRepository.saveAll(imagesToSave);
        log.info("[POST]-[PostService]-[createPost] imageUrl save success");


        //4. Hash Tag processing
        //hash태그 저장 프로세스 논의가 필요할듯
        //post hash tag 저장 (hashtag가 있는지 없는지 확인)
        /*
        Collections.sort(hashTags);
        List<Hashtag> existingHashTags = hashtagRepository.findByNameInOrderByNameAsc(hashTags);

        // 존재하지 않는 해시태그를 필터링
        List<String> nonExistingHashTags = hashTags.stream()
                .filter(tag -> !existingHashTags.contains(tag))
                .toList();

        List<PostHashtag> hashTagsToSave = new ArrayList<>();

        for(String hashTag : hashTags){
            if(nonExistingHashTags.contains(hashTag)){
                //save
                new Hashtag
            }

            PostHashtag hashtagToSave = new PostHashtag(null, savedPost,); //id는 왜 있지
            hashTagsToSave.add(hashtagToSave);

        }

         */

        return savedPost.getId();

    }
}
