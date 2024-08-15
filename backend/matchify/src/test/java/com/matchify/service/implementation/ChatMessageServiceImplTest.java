package com.matchify.service.implementation;

import com.matchify.model.ChatMessage;
import com.matchify.repository.ChatMessageRepository;
import com.matchify.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ChatMessageServiceImplTest {
    private ChatMessageServiceImpl chatMessageService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomService chatRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatMessageService = new ChatMessageServiceImpl(chatMessageRepository, chatRoomService);
    }

    @Test
    void save() {
        // Arrange
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId("senderId");
        chatMessage.setRecipientId("recipientId");

        when(chatRoomService.getChatRoomId(anyString(), anyString(), anyBoolean())).thenReturn(Optional.of("chatId"));

        // Act
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // Assert
        assertEquals("chatId", savedMessage.getChatId());
        verify(chatMessageRepository, times(1)).save(chatMessage);
    }

    @Test
    void findChatMessages() {
        // Arrange
        String senderId = "senderId";
        String recipientId = "recipientId";
        List<ChatMessage> expectedMessages = new ArrayList<>();
        expectedMessages.add(new ChatMessage());

        when(chatRoomService.getChatRoomId(senderId, recipientId, false)).thenReturn(Optional.of("chatId"));
        when(chatMessageRepository.findByChatId("chatId")).thenReturn(expectedMessages);

        // Act
        List<ChatMessage> actualMessages = chatMessageService.findChatMessages(senderId, recipientId);

        // Assert
        assertEquals(expectedMessages, actualMessages);
        verify(chatMessageRepository, times(1)).findByChatId("chatId");
    }
}
