package com.matchify.repository;

import com.matchify.model.ChatMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long>{
    List<ChatMessage> findByChatId(String s);
}

