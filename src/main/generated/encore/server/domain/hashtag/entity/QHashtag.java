package encore.server.domain.hashtag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHashtag is a Querydsl query type for Hashtag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHashtag extends EntityPathBase<Hashtag> {

    private static final long serialVersionUID = -1703033752L;

    public static final QHashtag hashtag = new QHashtag("hashtag");

    public final encore.server.global.common.QBaseTimeEntity _super = new encore.server.global.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public QHashtag(String variable) {
        super(Hashtag.class, forVariable(variable));
    }

    public QHashtag(Path<? extends Hashtag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHashtag(PathMetadata metadata) {
        super(Hashtag.class, metadata);
    }

}

