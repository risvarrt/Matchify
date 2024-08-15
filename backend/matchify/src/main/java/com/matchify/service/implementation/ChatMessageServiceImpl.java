package com.matchify.service.implementation;

import com.matchify.model.ChatMessage;
import com.matchify.repository.ChatMessageRepository;
import com.matchify.service.ChatMessageService;
import com.matchify.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    /**
     * Save the chat message to the database
     * @param chatMessage the chat message to save
     * @return the saved chat message
     */
    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService.getChatRoomId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                true
        ).orElseThrow();

        chatMessage.setChatId(chatId);
        repository.save(chatMessage);

        return chatMessage;
    }

    /**
     * Find chat messages between two users
     * @param senderId the sender's id
     * @param recipientId the recipient's id
     * @return a list of chat messages between the two users
     */
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(
                senderId,
                recipientId,
                false);

        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }

}
