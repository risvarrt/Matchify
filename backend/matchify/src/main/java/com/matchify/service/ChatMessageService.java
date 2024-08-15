package com.matchify.service;

import com.matchify.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findChatMessages(String senderId, String recipientId);
}
