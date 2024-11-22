package encore.server.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 1096887490L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final encore.server.global.common.QBaseTimeEntity _super = new encore.server.global.common.QBaseTimeEntity(this);

    public final EnumPath<encore.server.domain.post.enumerate.Category> category = createEnum("category", encore.server.domain.post.enumerate.Category.class);

    public final NumberPath<Long> commentCount = createNumber("commentCount", Long.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isNotice = createBoolean("isNotice");

    public final BooleanPath isTemporarySave = createBoolean("isTemporarySave");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final ListPath<encore.server.domain.hashtag.entity.PostHashtag, encore.server.domain.hashtag.entity.QPostHashtag> postHashtags = this.<encore.server.domain.hashtag.entity.PostHashtag, encore.server.domain.hashtag.entity.QPostHashtag>createList("postHashtags", encore.server.domain.hashtag.entity.PostHashtag.class, encore.server.domain.hashtag.entity.QPostHashtag.class, PathInits.DIRECT2);

    public final ListPath<PostImage, QPostImage> postImages = this.<PostImage, QPostImage>createList("postImages", PostImage.class, QPostImage.class, PathInits.DIRECT2);

    public final EnumPath<encore.server.domain.post.enumerate.PostType> postType = createEnum("postType", encore.server.domain.post.enumerate.PostType.class);

    public final StringPath title = createString("title");

    public final encore.server.domain.user.entity.QUser user;

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new encore.server.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

