package encore.server.domain.ticket.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTicket is a Querydsl query type for Ticket
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTicket extends EntityPathBase<Ticket> {

    private static final long serialVersionUID = -2114109734L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTicket ticket = new QTicket("ticket");

    public final encore.server.global.common.QBaseTimeEntity _super = new encore.server.global.common.QBaseTimeEntity(this);

    public final ListPath<Companion, QCompanion> companions = this.<Companion, QCompanion>createList("companions", Companion.class, QCompanion.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath title = createString("title");

    public final encore.server.domain.user.entity.QUser user;

    public final DateTimePath<java.time.LocalDateTime> viewedDate = createDateTime("viewedDate", java.time.LocalDateTime.class);

    public QTicket(String variable) {
        this(Ticket.class, forVariable(variable), INITS);
    }

    public QTicket(Path<? extends Ticket> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTicket(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTicket(PathMetadata metadata, PathInits inits) {
        this(Ticket.class, metadata, inits);
    }

    public QTicket(Class<? extends Ticket> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new encore.server.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

