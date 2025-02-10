package com.example.lovekeeper.domain.couple.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Status;
import com.example.lovekeeper.domain.note.model.Note;
import com.example.lovekeeper.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name = "couple")
public class Couple extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "couple_id")
	private Long id;

	@Builder.Default
	@OneToMany(mappedBy = "couple")
	private List<Member> members = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "couple", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Letter> letters = new ArrayList<>(); // 편지

	@Builder.Default
	@OneToMany(mappedBy = "couple", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Note> notes = new ArrayList<>();

	@Builder.Default
	private LocalDate startedAt = LocalDate.now();

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;

	//== 비스니스 로직 ==//
	public void updateStartDate(LocalDate newStartDate) {
		this.startedAt = newStartDate;
	}
}
