package encore.server.domain.ticket.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompanion is a Querydsl query type for Companion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompanion extends EntityPathBase<Companion> {

    private static final long serialVersionUID = -1384262530L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCompanion companion = new QCompanion("companion");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QTicket ticket;

    public QCompanion(String variable) {
        this(Companion.class, forVariable(variable), INITS);
    }

    public QCompanion(Path<? extends Companion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCompanion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCompanion(PathMetadata metadata, PathInits inits) {
        this(Companion.class, metadata, inits);
    }

    public QCompanion(Class<? extends Companion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ticket = inits.isInitialized("ticket") ? new QTicket(forProperty("ticket"), inits.get("ticket")) : null;
    }

}

