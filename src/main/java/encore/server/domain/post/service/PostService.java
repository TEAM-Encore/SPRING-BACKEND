package encore.server.domain.post.service;

import encore.server.domain.comment.repository.CommentRepository;
import encore.server.domain.hashtag.converter.PostHashtagConverter;
import encore.server.domain.hashtag.entity.Hashtag;
import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.hashtag.repository.HashtagRepository;
import encore.server.domain.hashtag.repository.PostHashtagRepository;
import encore.server.domain.term.entity.Term;
import encore.server.domain.term.repository.TermRepository;
import encore.server.domain.post.converter.PostConverter;
import encore.server.domain.post.converter.PostImageConverter;
import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.dto.request.PostUpdateReq;
import encore.server.domain.post.dto.response.PostDetailsGetRes;
import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostImage;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.post.repository.PostImageRepository;
import encore.server.domain.post.repository.PostLikeRepository;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.BadRequestException;
import encore.server.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final TermRepository musicalTermRepository;

    private final PostConverter postConverter;
    private final PostHashtagConverter postHashtagConverter;
    private final PostImageConverter postImageConverter;


    public PostDetailsGetRes getPostDetails(Long postId) {

        //Post Image와 User 를 Fetch Join 하여 Post 객체를 가져옴
        Post post = postRepository.findFetchJoinPostImageAndUserByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        //Post와 연관된 PostHashtag를 Fetch Join 하여 가져옴
        List<PostHashtag> allByPost = postHashtagRepository.findFetchHashtagAllByPostAndDeletedAtIsNull(post);

        //Post Image와 PostHashtag를 List<String> 으로 변환
        List<String> stringListFromPostHashtag = postHashtagConverter.stringListFrom(allByPost);
        List<String> stringListFromPostImage = postImageConverter.stringListFrom(post.getPostImages());

        //Postlike 에서 Post 의 likeCount를 수정하는 로직이 있나?
        Integer numOfLike = postLikeRepository.countByPostAndDeletedAtIsNull(post);
        Long numOfComment = commentRepository.countByPostAndDeletedAtIsNull(post);


        //**userEntity 에 Name 필드 추가 필요
        //PostDetailGetRes 생성하여 return
        return postConverter.postDetailsGetResFrom(post, stringListFromPostHashtag,
                stringListFromPostImage, numOfLike, numOfComment, Collections.emptyList());
    }

    @Transactional
    public void deletePost(Long postId) {

        if(!postRepository.existsById(postId)){
            throw new BadRequestException("Post does not exist");
        }

        postHashtagRepository.softDeleteByPostId(postId);
        postImageRepository.softDeleteByPostId(postId);
        postRepository.softDeleteByPostId(postId);
        postLikeRepository.softDeleteByPostId(postId);
        commentRepository.softDeleteByPostId(postId);

    }

    @Transactional
    public Long updatePost(Long postIdToUpdate, PostUpdateReq postUpdateReq) {

        log.info("[POST]-[PostService]-[updatePost] method call");

        // 1. Read target Post ID and Validation
        Long postId = postIdToUpdate;

        if(!postRepository.existsById(postId)){
            throw new BadRequestException("Post does not exist");
        }


        // 2. Update Post (영속성 컨텍스트 초기화)
        int numOfUpdatedPost = postRepository.updatePost(Category.valueOf(postUpdateReq.category()), PostType.valueOf(postUpdateReq.postType()),
                postUpdateReq.title(), postUpdateReq.content(), postUpdateReq.isNotice(), postUpdateReq.isTemporarySave(), postId);

        log.info("[POST]-[PostService]-[updatePost] Post updated successfully. post_id: {}", postId);

        // 3. PostImg update
        // Post와 관련된 이미지 전부 삭제 (영속성 컨텍스트 초기화)
        postImageRepository.softDeleteByPostId(postId);
        log.info("[POST]-[PostService]-[updatePost] PostImage deleted successfully");

        //업데이트할 이미지 url 들
        List<String> imgUrls = postUpdateReq.imgUrls();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        //이미지 업데이트
        List<PostImage> imagesToSave = new ArrayList<>();
        for(String imgUrl : imgUrls){
            PostImage imageToSave = new PostImage(null, imgUrl, post); //id는 왜 있지
            imagesToSave.add(imageToSave);
        }
        postImageRepository.saveAll(imagesToSave);

        log.info("[POST]-[PostService]-[updatePost] PostImage saved successfully");

        //4. hashtag update
        List<String> hashTags = postUpdateReq.hashTags();
        //전체 postHashTag 삭제 (삭제 후 재저장)
        postHashtagRepository.softDeleteByPostId(postId);

        // 해시태그 테이블에 존재하는 해시태그
        List<Hashtag> existingHashTags = hashtagRepository.findByNameInAndDeletedAtIsNull(hashTags);
        List<String> existingHashTagsStringList = existingHashTags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());

        // 해시태그 테이블에 존재하지 않는 해시태그를 필터링
        List<String> nonExistingHashTags = hashTags.stream()
                .filter(tag -> !existingHashTagsStringList.contains(tag))
                .toList();

        List<Hashtag> hashtagsToSave = new ArrayList<>();

        for(String hashTagToSave : nonExistingHashTags){
            hashtagsToSave.add(new Hashtag(hashTagToSave));
        }

        List<Hashtag> savedHashTags = hashtagRepository.saveAll(hashtagsToSave);

        Post savedPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        List<PostHashtag> phashTagsToSave = new ArrayList<>();

        for(Hashtag hashTag : existingHashTags){
            PostHashtag pHashtagToSave = new PostHashtag(null, savedPost,hashTag); //id는 왜 있지
            phashTagsToSave.add(pHashtagToSave);
        }
        for(Hashtag hashTag : savedHashTags) {
            PostHashtag pHashtagToSave = new PostHashtag(null, savedPost, hashTag); //id는 왜 있지
            phashTagsToSave.add(pHashtagToSave);
        }

        postHashtagRepository.saveAll(phashTagsToSave);


        return postId;

    }

    @Transactional
    public Long createPost(PostCreateReq postCreateReq, Long userId) {

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

        for (String imgUrl : imgUrls) {
            PostImage imageToSave = new PostImage(null, imgUrl, savedPost); //id는 왜 있지
            imagesToSave.add(imageToSave);
        }

        postImageRepository.saveAll(imagesToSave);
        log.info("[POST]-[PostService]-[createPost] imageUrl save success");


        //4. Hash Tag processing
        //post hash tag 저장 (hashtag가 있는지 없는지 확인)

        // 해시태그 테이블에 존재하는 해시태그
        List<Hashtag> existingHashTags = hashtagRepository.findByNameInAndDeletedAtIsNull(hashTags);
        List<String> existingHashTagsStringList = existingHashTags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());

        // 해시태그 테이블에 존재하지 않는 해시태그를 필터링
        List<String> nonExistingHashTags = hashTags.stream()
                .filter(tag -> !existingHashTagsStringList.contains(tag))
                .toList();

        List<Hashtag> hashtagsToSave = new ArrayList<>();

        for (String hashTagToSave : nonExistingHashTags) {
            hashtagsToSave.add(new Hashtag(hashTagToSave));
        }

        List<Hashtag> savedHashTags = hashtagRepository.saveAll(hashtagsToSave);


        List<PostHashtag> phashTagsToSave = new ArrayList<>();

        for (Hashtag hashTag : existingHashTags) {
            PostHashtag pHashtagToSave = new PostHashtag(null, savedPost, hashTag); //id는 왜 있지
            phashTagsToSave.add(pHashtagToSave);
        }
        for (Hashtag hashTag : savedHashTags) {
            PostHashtag pHashtagToSave = new PostHashtag(null, savedPost, hashTag); //id는 왜 있지
            phashTagsToSave.add(pHashtagToSave);
        }

        // 시작 시간 측정
        long startTime = System.currentTimeMillis();

// content를 기준으로 MusicalTerm 조회
        List<Term> matchingTerms = musicalTermRepository.findByTermIn(post.getContent());

// MusicalTerm이 존재하면 content에 태그 추가
        if (!matchingTerms.isEmpty()) {
            String updatedContent = musicalTermRepository.highlightTermsInContent(post.getContent());
            post.updateContent(updatedContent); // 수정된 content를 post에 반영
        }

// MusicalTerm 저장
        musicalTermRepository.saveAll(matchingTerms);

// 종료 시간 측정
        long endTime = System.currentTimeMillis();

// 소요 시간 출력
        System.out.println("Execution Time: " + (endTime - startTime) + " ms");

// PostHashtags 저장
        postHashtagRepository.saveAll(phashTagsToSave);

// Post ID 반환
        return savedPost.getId();
    }

    public Slice<SimplePostRes> getPostPagination(Long cursor, String category,
                                                  String type, String searchWord, Pageable pageable) {

        return postRepository.findPostsByCursor(cursor, category, type, searchWord, pageable);
    }

    public Slice<SimplePostRes> getPostPaginationByHashtag(Long cursor, String hashtag, Pageable pageable) {
        return postRepository.findPostsByHashtag(cursor, hashtag, pageable);
    }
}