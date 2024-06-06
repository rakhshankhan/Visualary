package com.rakhshankhan.visualary.model.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    Chat findByChatIdAndOwner(Integer chatId, User owner);

    List<Chat> findByOwner(User owner);

    @Transactional
    void deleteByOwner(User owner);

}
