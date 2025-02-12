package com.example.lovekeeper.domain.couple.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouple is a Querydsl query type for Couple
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouple extends EntityPathBase<Couple> {

    private static final long serialVersionUID = 95586722L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouple couple = new QCouple("couple");

    public final com.example.lovekeeper.global.common.QBaseEntity _super = new com.example.lovekeeper.global.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.example.lovekeeper.domain.letter.model.Letter, com.example.lovekeeper.domain.letter.model.QLetter> letters = this.<com.example.lovekeeper.domain.letter.model.Letter, com.example.lovekeeper.domain.letter.model.QLetter>createList("letters", com.example.lovekeeper.domain.letter.model.Letter.class, com.example.lovekeeper.domain.letter.model.QLetter.class, PathInits.DIRECT2);

    public final com.example.lovekeeper.domain.member.model.QMember member1;

    public final com.example.lovekeeper.domain.member.model.QMember member2;

    public final ListPath<com.example.lovekeeper.domain.note.model.Note, com.example.lovekeeper.domain.note.model.QNote> notes = this.<com.example.lovekeeper.domain.note.model.Note, com.example.lovekeeper.domain.note.model.QNote>createList("notes", com.example.lovekeeper.domain.note.model.Note.class, com.example.lovekeeper.domain.note.model.QNote.class, PathInits.DIRECT2);

    public final ListPath<com.example.lovekeeper.domain.promise.model.Promise, com.example.lovekeeper.domain.promise.model.QPromise> promises = this.<com.example.lovekeeper.domain.promise.model.Promise, com.example.lovekeeper.domain.promise.model.QPromise>createList("promises", com.example.lovekeeper.domain.promise.model.Promise.class, com.example.lovekeeper.domain.promise.model.QPromise.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> startedAt = createDate("startedAt", java.time.LocalDate.class);

    public final EnumPath<CoupleStatus> status = createEnum("status", CoupleStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCouple(String variable) {
        this(Couple.class, forVariable(variable), INITS);
    }

    public QCouple(Path<? extends Couple> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouple(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouple(PathMetadata metadata, PathInits inits) {
        this(Couple.class, metadata, inits);
    }

    public QCouple(Class<? extends Couple> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member1 = inits.isInitialized("member1") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("member1"), inits.get("member1")) : null;
        this.member2 = inits.isInitialized("member2") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("member2"), inits.get("member2")) : null;
    }

}

