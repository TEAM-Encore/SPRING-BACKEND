package encore.server.domain.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = 1482025992L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final encore.server.global.common.QBaseTimeEntity _super = new encore.server.global.common.QBaseTimeEntity(this);

    public final ListPath<Comment, QComment> childComments = this.<Comment, QComment>createList("childComments", Comment.class, QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<CommentLike, QCommentLike> likes = this.<CommentLike, QCommentLike>createList("likes", CommentLike.class, QCommentLike.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QComment parentId;

    public final encore.server.domain.post.entity.QPost post;

    public final encore.server.domain.user.entity.QUser user;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentId = inits.isInitialized("parentId") ? new QComment(forProperty("parentId"), inits.get("parentId")) : null;
        this.post = inits.isInitialized("post") ? new encore.server.domain.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new encore.server.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

