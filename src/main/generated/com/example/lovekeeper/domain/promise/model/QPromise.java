package com.example.lovekeeper.domain.promise.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPromise is a Querydsl query type for Promise
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPromise extends EntityPathBase<Promise> {

    private static final long serialVersionUID = -444945454L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPromise promise = new QPromise("promise");

    public final com.example.lovekeeper.global.common.QBaseEntity _super = new com.example.lovekeeper.global.common.QBaseEntity(this);

    public final StringPath content = createString("content");

    public final com.example.lovekeeper.domain.couple.model.QCouple couple;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.lovekeeper.domain.member.model.QMember member;

    public final DatePath<java.time.LocalDate> promiseDate = createDate("promiseDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPromise(String variable) {
        this(Promise.class, forVariable(variable), INITS);
    }

    public QPromise(Path<? extends Promise> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPromise(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPromise(PathMetadata metadata, PathInits inits) {
        this(Promise.class, metadata, inits);
    }

    public QPromise(Class<? extends Promise> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.couple = inits.isInitialized("couple") ? new com.example.lovekeeper.domain.couple.model.QCouple(forProperty("couple"), inits.get("couple")) : null;
        this.member = inits.isInitialized("member") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

