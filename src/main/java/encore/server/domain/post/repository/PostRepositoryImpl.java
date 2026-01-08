package encore.server.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostImage;
import encore.server.domain.post.entity.PostTerm;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.review.entity.Review;
import encore.server.domain.term.entity.Term;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static encore.server.domain.hashtag.entity.QPostHashtag.postHashtag;
import static encore.server.domain.post.entity.QPostTerm.postTerm;
import static encore.server.domain.review.entity.QReview.review;
import static encore.server.domain.term.entity.QTerm.term;
import static encore.server.domain.post.entity.QPost.post;
import static encore.server.domain.post.entity.QPostImage.postImage;
import static encore.server.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Post> findPostsByCursor(Long cursor, String category, String type,
                                                  String searchWord, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(addCursorCondition(cursor))
                .and(addCategoryCondition(category))
                .and(addTypeCondition(type))
                .and(addSearchWordCondition(searchWord))
                .and(post.isTemporarySave.eq(false))
                .and(post.deletedAt.isNull());

        List<Post> posts = queryFactory.selectFrom(post)
                .where(predicate)
                .leftJoin(post.user, user).fetchJoin()
                .orderBy(getSortOrder(pageable))
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return posts;

    }

    public List<Post> findPostsByHashtag(Long cursor, String hashtag, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(addCursorCondition(cursor))
                .and(addHashtagCondition(hashtag))
                .and(post.isTemporarySave.eq(false))
                .and(post.deletedAt.isNull());

        List<Post> posts = queryFactory.selectFrom(post)
                .where(predicate)
                .leftJoin(post.user, user).fetchJoin()
                .orderBy(getSortOrder(pageable))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return posts;
    }

    public Optional<Post> findFetchJoinPostImageAndUserByIdAndDeletedAtIsNull(Long postId) {
        // Post와 User 정보 페치 조인
        Post fetchedPost = queryFactory.selectFrom(post)
                .where(post.id.eq(postId).and(post.deletedAt.isNull()))
                .leftJoin(post.user).fetchJoin()
                .fetchOne();

        if (fetchedPost == null) {
            return Optional.empty();
        }

        // PostImages 가져오기
        List<PostImage> postImages = queryFactory.selectFrom(postImage)
                .where(postImage.post.eq(fetchedPost))
                .fetch();

        // Term 가져오기
        List<Term> musicalTerms = queryFactory.selectFrom(term)
                .join(term.posts, postTerm)
                .join(postTerm.post, post)
                .where(post.eq(fetchedPost))
                .fetch();
        fetchedPost.addMusicalTerms(musicalTerms);

        // PostHashtags 가져오기
        List<PostHashtag> postHashtags = queryFactory.selectFrom(postHashtag)
                .where(postHashtag.post.eq(fetchedPost))
                .fetch();

        return Optional.of(fetchedPost);
    }

    private OrderSpecifier<?>[] getSortOrder(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    switch (order.getProperty().toLowerCase()) {
                        case "createdat":
                            return new OrderSpecifier<>(Order.DESC, post.id);
                        case "likecount":
                            return new OrderSpecifier<>(Order.DESC, post.likeCount);
                        default:
                            return new OrderSpecifier<>(Order.DESC, post.id);
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }

    // cursor 뒤인지 확인(id 기준)
    private BooleanExpression addCursorCondition(Long cursor) {
        if (cursor != null) {
            return post.id.lt(cursor);
        }
        return null;
    }

    private BooleanExpression addCategoryCondition(String category) {
        if (category != null && !category.isEmpty()) {
            return post.category.eq(Category.valueOf(category));
        }
        return null;
    }

    private BooleanExpression addTypeCondition(String type) {
        if (type != null && !type.isEmpty()) {
            return post.postType.eq(PostType.valueOf(type));
        }
        return null;
    }

    private BooleanExpression addSearchWordCondition(String searchWord) {
        if (searchWord != null && !searchWord.isEmpty()) {
            return post.title.containsIgnoreCase(searchWord)
                    .or(post.content.containsIgnoreCase(searchWord));
        }
        return null;
    }

    private BooleanExpression addHashtagCondition(String hashtag) {
        if (hashtag != null && !hashtag.isEmpty()) {
            return post.postHashtags.any().hashtag.name.eq(hashtag);
        }
        return null;
    }

    public List<Post> findByPostAutoCompleteSuggestions(Long userId, String keyword) {
        return queryFactory
                .selectFrom(post)
                .where(post.user.id.eq(userId)
                                .and(post.deletedAt.isNull())
                                .and(post.title.containsIgnoreCase(keyword)
                                                .or(post.content.containsIgnoreCase(keyword))))
                .limit(5)
                .fetch();
    }

}