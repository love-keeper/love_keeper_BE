package com.example.lovekeeper.domain.connectionhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory;

public interface ConnectionHistoryRepository extends JpaRepository<ConnectionHistory, Long> {
}
