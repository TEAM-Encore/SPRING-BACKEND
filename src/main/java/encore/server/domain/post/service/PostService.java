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
import encore.server.domain.post.entity.PostLike;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.post.repository.PostImageRepository;
import encore.server.domain.post.repository.PostLikeRepository;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.domain.user.service.UserFcmService;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.BadRequestException;
import encore.server.global.exception.ErrorCode;
import encore.server.global.exception.UserNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
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
    private final UserFcmService userFcmService;


    public PostDetailsGetRes getPostDetails(Long postId, Long userId) {

        // validation: post, user 존재성 확인
        // Post Image, User 를 Fetch Join 하여 Post 객체를 가져옴
        Post post = postRepository.findFetchJoinPostImageAndUserByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        User userViewing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        //Post 와 연관된 PostHashtag 를 Fetch Join 하여 가져옴
        List<PostHashtag> allByPost = postHashtagRepository.findFetchHashtagAllByPostAndDeletedAtIsNull(post);

        // business logic:
        //Post Image와 PostHashtag를 List<String> 으로 변환
        List<String> stringListFromPostHashtag = postHashtagConverter.stringListFrom(allByPost);
        List<String> stringListFromPostImage = postImageConverter.stringListFrom(post.getPostImages());

        //post 엔티티 필드에 있는 count 로 대체
        //Integer numOfLike = postLikeRepository.countByPostAndDeletedAtIsNull(post);
        //Integer numOfComment = commentRepository.countByPostAndDeletedAtIsNull(post);

        //Post 에 좋아요를 눌렀는지 확인
        boolean isLiked = false;
        Optional<PostLike> optionalPostLike = postLikeRepository.findByUserAndPost(userViewing, post);
        if (optionalPostLike.isPresent()) {
            isLiked = optionalPostLike.get().isLiked();
        }

        // return: 게시글 상세정보 반환
        return postConverter.postDetailsGetResFrom(post, stringListFromPostHashtag,
                stringListFromPostImage, post.getLikeCount(), post.getCommentCount(), isLiked, Collections.emptyList());
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

        userFcmService.notifyUsersByHashtag(post, phashTagsToSave);

        // content를 기준으로 MusicalTerm 조회
        List<Term> matchingTerms = musicalTermRepository.findByTermIn(post.getContent());

        // MusicalTerm이 존재하면 content에 태그 추가
        if (!matchingTerms.isEmpty()) {
            String updatedContent = musicalTermRepository.highlightTermsInContent(post.getContent());
            post.updateContent(updatedContent); // 수정된 content를 post에 반영
        }

        matchingTerms.forEach(post::addMusicalTerm);

        // MusicalTerm 저장
        musicalTermRepository.saveAll(matchingTerms);

        // PostHashtags 저장
        postHashtagRepository.saveAll(phashTagsToSave);

        // Post ID 반환
        return savedPost.getId();
    }

    public Slice<SimplePostRes> getPostPagination(Long cursor, String category,
                                                  String type, String searchWord, Pageable pageable, Long userId) {
        //business logic
        // 1. Cursor 기반으로 게시물 목록 가져오기
        List<Post> posts = postRepository.findPostsByCursor(cursor, category, type, searchWord, pageable);
        // 2 현재 로그인한 사용자 정보 조회
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("Post not found"));
        // 3. 사용자가 해당 게시물들에 눌렀던 좋아요 기록 가져오기. 좋아요가 눌러진 경우만 가져오기
        List<PostLike> postLikes = postLikeRepository.findByUserAndPostIn(user, posts)
                .stream()
                .filter(PostLike::isLiked)
                .toList();
        // 4. 좋아요를 누른 게시물만 HashSet 으로 저장
        Set<Post> likedPostMap = postLikes.stream()
                .map(PostLike::getPost)
                .collect(Collectors.toSet());

        boolean hasNext = posts.size() > pageable.getPageSize();

        List<SimplePostRes> postResponses = posts.stream()
                .limit(pageable.getPageSize())
                .map(post -> PostConverter.toSimplePostRes(post, post.getUser(), likedPostMap.contains(post)))
                .collect(Collectors.toList());

        return new SliceImpl<>(postResponses, pageable, hasNext);
    }

    public Slice<SimplePostRes> getPostPaginationByHashtag(Long cursor, String hashtag, Pageable pageable, Long userId) {

        //business logic
        // 1. Cursor 기반으로 게시물 목록 가져오기
        List<Post> posts = postRepository.findPostsByHashtag(cursor, hashtag, pageable);
        // 2 현재 로그인한 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        // 3. 사용자가 해당 게시물들에 눌렀던 좋아요 기록 가져오기. 좋아요가 눌러진 경우만 가져오기
        List<PostLike> postLikes = postLikeRepository.findByUserAndPostIn(user, posts)
                .stream()
                .filter(PostLike::isLiked)
                .toList();
        // 4. 좋아요를 누른 게시물만 HashSet 으로 저장
        Set<Post> likedPostMap = postLikes.stream()
                .map(PostLike::getPost)
                .collect(Collectors.toSet());

        boolean hasNext = posts.size() > pageable.getPageSize();

        List<SimplePostRes> postResponses = posts.stream()
                .limit(pageable.getPageSize())
                .map(post -> PostConverter.toSimplePostRes(post, post.getUser(), likedPostMap.contains(post)))
                .collect(Collectors.toList());

        return new SliceImpl<>(postResponses, pageable, hasNext);
    }

    /**
     * 사용자가 작성한 게시글을 커서 기반으로 페이징 조회합니다.
     *
     * @param userId 사용자 ID
     * @param cursor 기준 시간 (null일 경우 최신부터 조회)
     * @param size   페이지 크기
     * @return 사용자의 게시글 목록 (SimplePostRes 형태)의 Slice
     */
    public Slice<SimplePostRes> getMyPostPagination(Long userId, LocalDateTime cursor, int size) {

        // 1. 페이징 및 정렬 정보 설정 (최신순 정렬)
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());

        // 2. userId에 해당하는 유저 조회 (없으면 예외 발생)
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // 3. cursor가 null이면 최신 글부터 조회, 아니면 cursor 기준 이전 글 조회
        List<Post> posts;
        if (cursor == null) {
            posts = postRepository.findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(user, pageable);
        } else {
            posts = postRepository.findByUserAndCreatedAtBeforeAndDeletedAtIsNullOrderByCreatedAtDesc(user, cursor, pageable);
        }

        // 4. 사용자가 해당 게시물에 눌렀던 좋아요 가져오기 (좋아요가 눌러진 경우만)
        List<PostLike> postLikes = postLikeRepository.findByUserAndPostIn(user, posts)
            .stream()
            .filter(PostLike::isLiked)
            .toList();

        // 5. 좋아요를 누른 게시물만 Set으로 저장하여 빠른 조회 가능하게 함
        Set<Post> likedPostMap = postLikes.stream()
            .map(PostLike::getPost)
            .collect(Collectors.toSet());

        // 6. 다음 페이지 여부 판단 (조회한 posts 개수가 페이지 사이즈보다 크면 next 있음)
        boolean hasNext = posts.size() > pageable.getPageSize();

        // 7. SimplePostRes로 변환 + 좋아요 여부 포함해서 응답 객체 생성
        List<SimplePostRes> postResponses = posts.stream()
            .limit(pageable.getPageSize())
            .map(post -> PostConverter.toSimplePostRes(post, post.getUser(), likedPostMap.contains(post)))
            .collect(Collectors.toList());

        // 8. Slice 형태로 결과 반환 (hasNext 포함)
        return new SliceImpl<>(postResponses, pageable, hasNext);
    }
}