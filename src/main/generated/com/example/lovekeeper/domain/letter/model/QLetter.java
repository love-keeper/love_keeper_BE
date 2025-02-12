package com.example.lovekeeper.domain.letter.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLetter is a Querydsl query type for Letter
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLetter extends EntityPathBase<Letter> {

    private static final long serialVersionUID = -626237598L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLetter letter = new QLetter("letter");

    public final com.example.lovekeeper.global.common.QBaseEntity _super = new com.example.lovekeeper.global.common.QBaseEntity(this);

    public final StringPath content = createString("content");

    public final com.example.lovekeeper.domain.couple.model.QCouple couple;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.lovekeeper.domain.member.model.QMember receiver;

    public final com.example.lovekeeper.domain.member.model.QMember sender;

    public final DatePath<java.time.LocalDate> sentDate = createDate("sentDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QLetter(String variable) {
        this(Letter.class, forVariable(variable), INITS);
    }

    public QLetter(Path<? extends Letter> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLetter(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLetter(PathMetadata metadata, PathInits inits) {
        this(Letter.class, metadata, inits);
    }

    public QLetter(Class<? extends Letter> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.couple = inits.isInitialized("couple") ? new com.example.lovekeeper.domain.couple.model.QCouple(forProperty("couple"), inits.get("couple")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("receiver"), inits.get("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("sender"), inits.get("sender")) : null;
    }

}

