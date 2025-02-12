package com.example.lovekeeper.domain.draft.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDraft is a Querydsl query type for Draft
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDraft extends EntityPathBase<Draft> {

    private static final long serialVersionUID = -883292834L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDraft draft = new QDraft("draft");

    public final com.example.lovekeeper.global.common.QBaseEntity _super = new com.example.lovekeeper.global.common.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Integer> draftOrder = createNumber("draftOrder", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.lovekeeper.domain.member.model.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDraft(String variable) {
        this(Draft.class, forVariable(variable), INITS);
    }

    public QDraft(Path<? extends Draft> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDraft(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDraft(PathMetadata metadata, PathInits inits) {
        this(Draft.class, metadata, inits);
    }

    public QDraft(Class<? extends Draft> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

