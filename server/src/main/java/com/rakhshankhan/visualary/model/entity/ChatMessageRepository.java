package com.rakhshankhan.visualary.model.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findAllByChatIdOrderByTimestampAsc(Integer chatId);

}
