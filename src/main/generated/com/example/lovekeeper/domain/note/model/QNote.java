package com.example.lovekeeper.domain.note.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNote is a Querydsl query type for Note
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNote extends EntityPathBase<Note> {

    private static final long serialVersionUID = 394462882L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNote note = new QNote("note");

    public final com.example.lovekeeper.global.common.QBaseEntity _super = new com.example.lovekeeper.global.common.QBaseEntity(this);

    public final StringPath content = createString("content");

    public final com.example.lovekeeper.domain.couple.model.QCouple couple;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final com.example.lovekeeper.domain.member.model.QMember receiver;

    public final com.example.lovekeeper.domain.member.model.QMember sender;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QNote(String variable) {
        this(Note.class, forVariable(variable), INITS);
    }

    public QNote(Path<? extends Note> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNote(PathMetadata metadata, PathInits inits) {
        this(Note.class, metadata, inits);
    }

    public QNote(Class<? extends Note> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.couple = inits.isInitialized("couple") ? new com.example.lovekeeper.domain.couple.model.QCouple(forProperty("couple"), inits.get("couple")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("receiver"), inits.get("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("sender"), inits.get("sender")) : null;
    }

}

