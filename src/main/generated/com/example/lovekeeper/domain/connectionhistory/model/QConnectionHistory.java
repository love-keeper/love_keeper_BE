package com.example.lovekeeper.domain.connectionhistory.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConnectionHistory is a Querydsl query type for ConnectionHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConnectionHistory extends EntityPathBase<ConnectionHistory> {

    private static final long serialVersionUID = 219306216L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConnectionHistory connectionHistory = new QConnectionHistory("connectionHistory");

    public final com.example.lovekeeper.global.common.QBaseEntity _super = new com.example.lovekeeper.global.common.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> connectedAt = createDate("connectedAt", java.time.LocalDate.class);

    public final com.example.lovekeeper.domain.couple.model.QCouple couple;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final DatePath<java.time.LocalDate> disconnectedAt = createDate("disconnectedAt", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.lovekeeper.domain.member.model.QMember member1;

    public final com.example.lovekeeper.domain.member.model.QMember member2;

    public final DatePath<java.time.LocalDate> reconnectedAt = createDate("reconnectedAt", java.time.LocalDate.class);

    public final EnumPath<com.example.lovekeeper.domain.couple.model.CoupleStatus> status = createEnum("status", com.example.lovekeeper.domain.couple.model.CoupleStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QConnectionHistory(String variable) {
        this(ConnectionHistory.class, forVariable(variable), INITS);
    }

    public QConnectionHistory(Path<? extends ConnectionHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConnectionHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConnectionHistory(PathMetadata metadata, PathInits inits) {
        this(ConnectionHistory.class, metadata, inits);
    }

    public QConnectionHistory(Class<? extends ConnectionHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.couple = inits.isInitialized("couple") ? new com.example.lovekeeper.domain.couple.model.QCouple(forProperty("couple"), inits.get("couple")) : null;
        this.member1 = inits.isInitialized("member1") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("member1"), inits.get("member1")) : null;
        this.member2 = inits.isInitialized("member2") ? new com.example.lovekeeper.domain.member.model.QMember(forProperty("member2"), inits.get("member2")) : null;
    }

}

