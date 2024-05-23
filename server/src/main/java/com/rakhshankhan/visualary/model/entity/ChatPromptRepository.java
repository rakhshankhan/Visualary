package com.rakhshankhan.visualary.model.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatPromptRepository extends JpaRepository<ChatPrompt, Integer> {
    List<ChatPrompt> findAllByOrderByTimestampAsc();

}
