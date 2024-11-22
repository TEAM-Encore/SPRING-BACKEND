package encore.server.domain.musical.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMusical is a Querydsl query type for Musical
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMusical extends EntityPathBase<Musical> {

    private static final long serialVersionUID = -1987996696L;

    public static final QMusical musical = new QMusical("musical");

    public final encore.server.global.common.QBaseTimeEntity _super = new encore.server.global.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath location = createString("location");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QMusical(String variable) {
        super(Musical.class, forVariable(variable));
    }

    public QMusical(Path<? extends Musical> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMusical(PathMetadata metadata) {
        super(Musical.class, metadata);
    }

}

