package com.example.lovekeeper.domain.member.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1725858846L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final com.example.lovekeeper.global.common.QBaseEntity _super = new com.example.lovekeeper.global.common.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final ListPath<com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory, com.example.lovekeeper.domain.connectionhistory.model.QConnectionHistory> connectionHistories1 = this.<com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory, com.example.lovekeeper.domain.connectionhistory.model.QConnectionHistory>createList("connectionHistories1", com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory.class, com.example.lovekeeper.domain.connectionhistory.model.QConnectionHistory.class, PathInits.DIRECT2);

    public final ListPath<com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory, com.example.lovekeeper.domain.connectionhistory.model.QConnectionHistory> connectionHistories2 = this.<com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory, com.example.lovekeeper.domain.connectionhistory.model.QConnectionHistory>createList("connectionHistories2", com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory.class, com.example.lovekeeper.domain.connectionhistory.model.QConnectionHistory.class, PathInits.DIRECT2);

    public final EnumPath<com.example.lovekeeper.domain.couple.model.CoupleStatus> coupleStatus = createEnum("coupleStatus", com.example.lovekeeper.domain.couple.model.CoupleStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final com.example.lovekeeper.domain.draft.model.QDraft draft;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath inviteCode = createString("inviteCode");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final ListPath<com.example.lovekeeper.domain.promise.model.Promise, com.example.lovekeeper.domain.promise.model.QPromise> promises = this.<com.example.lovekeeper.domain.promise.model.Promise, com.example.lovekeeper.domain.promise.model.QPromise>createList("promises", com.example.lovekeeper.domain.promise.model.Promise.class, com.example.lovekeeper.domain.promise.model.QPromise.class, PathInits.DIRECT2);

    public final EnumPath<Provider> provider = createEnum("provider", Provider.class);

    public final StringPath providerId = createString("providerId");

    public final ListPath<com.example.lovekeeper.domain.letter.model.Letter, com.example.lovekeeper.domain.letter.model.QLetter> receivedLetters = this.<com.example.lovekeeper.domain.letter.model.Letter, com.example.lovekeeper.domain.letter.model.QLetter>createList("receivedLetters", com.example.lovekeeper.domain.letter.model.Letter.class, com.example.lovekeeper.domain.letter.model.QLetter.class, PathInits.DIRECT2);

    public final ListPath<com.example.lovekeeper.domain.note.model.Note, com.example.lovekeeper.domain.note.model.QNote> receivedNotes = this.<com.example.lovekeeper.domain.note.model.Note, com.example.lovekeeper.domain.note.model.QNote>createList("receivedNotes", com.example.lovekeeper.domain.note.model.Note.class, com.example.lovekeeper.domain.note.model.QNote.class, PathInits.DIRECT2);

    public final com.example.lovekeeper.domain.auth.model.QRefreshToken refreshToken;

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final ListPath<com.example.lovekeeper.domain.letter.model.Letter, com.example.lovekeeper.domain.letter.model.QLetter> sentLetters = this.<com.example.lovekeeper.domain.letter.model.Letter, com.example.lovekeeper.domain.letter.model.QLetter>createList("sentLetters", com.example.lovekeeper.domain.letter.model.Letter.class, com.example.lovekeeper.domain.letter.model.QLetter.class, PathInits.DIRECT2);

    public final ListPath<com.example.lovekeeper.domain.note.model.Note, com.example.lovekeeper.domain.note.model.QNote> sentNotes = this.<com.example.lovekeeper.domain.note.model.Note, com.example.lovekeeper.domain.note.model.QNote>createList("sentNotes", com.example.lovekeeper.domain.note.model.Note.class, com.example.lovekeeper.domain.note.model.QNote.class, PathInits.DIRECT2);

    public final EnumPath<MemberStatus> status = createEnum("status", MemberStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.draft = inits.isInitialized("draft") ? new com.example.lovekeeper.domain.draft.model.QDraft(forProperty("draft"), inits.get("draft")) : null;
        this.refreshToken = inits.isInitialized("refreshToken") ? new com.example.lovekeeper.domain.auth.model.QRefreshToken(forProperty("refreshToken"), inits.get("refreshToken")) : null;
    }

}

