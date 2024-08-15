package com.matchify.controller;

import com.matchify.model.ChatNotification;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.matchify.model.ChatMessage;
import com.matchify.service.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.List;

class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessMessage() {
        // Arrange
        ChatMessage chatMessage = new ChatMessage();
        ChatMessage savedMessage = new ChatMessage();

        when(chatMessageService.save(any(ChatMessage.class))).thenReturn(savedMessage);

        // Act
        chatController.processMessage(chatMessage);

        // Assert
        verify(chatMessageService, times(1)).save(chatMessage);
        verify(messagingTemplate, times(1)).convertAndSendToUser(
                eq(savedMessage.getRecipientId()),
                eq("/queue/messages"),
                any(ChatNotification.class));
    }

    @Test
    void testFindChatMessages() {
        // Arrange
        String senderId = "sender1";
        String recipientId = "recipient1";
        List<ChatMessage> chatMessages = List.of(new ChatMessage());

        when(chatMessageService.findChatMessages(senderId, recipientId)).thenReturn(chatMessages);

        // Act
        ResponseEntity<List<ChatMessage>> response = chatController.findChatMessages(senderId, recipientId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chatMessages, response.getBody());
        verify(chatMessageService, times(1)).findChatMessages(senderId, recipientId);
    }
}
