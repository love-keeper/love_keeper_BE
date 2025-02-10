package com.example.lovekeeper.domain.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.note.model.Note;

public interface NoteJpaRepository extends JpaRepository<Note, Long> {
}
