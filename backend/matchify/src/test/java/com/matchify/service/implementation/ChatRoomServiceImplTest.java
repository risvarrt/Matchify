package com.matchify.service.implementation;

import com.matchify.model.ChatRoom;
import com.matchify.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRoomServiceImplTest {

    private ChatRoomServiceImpl chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void setUp() {
        AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this);
        chatRoomService = new ChatRoomServiceImpl(chatRoomRepository);
    }

    @Test
    void getChatRoomId_existingRoom() {
        // Arrange
        String senderId = "senderId";
        String recipientId = "recipientId";
        String chatId = "existingChatId";
        ChatRoom chatRoom = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.of(chatRoom));

        // Act
        Optional<String> result = chatRoomService.getChatRoomId(senderId, recipientId, false);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(chatId, result.get());
    }

    @Test
    void getChatRoomId_roomNotCreated() {
        // Arrange
        String senderId = "senderId";
        String recipientId = "recipientId";

        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.empty());

        // Act
        Optional<String> result = chatRoomService.getChatRoomId(senderId, recipientId, false);

        // Assert
        assertFalse(result.isPresent());
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verifyNoMoreInteractions(chatRoomRepository);
    }

    @Test
    void getChatRoomId_newRoom() {
        // Arrange
        String senderId = "senderId";
        String recipientId = "recipientId";
        String expectedChatId = senderId + "_" + recipientId;

        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<String> result = chatRoomService.getChatRoomId(senderId, recipientId, true);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedChatId, result.get());
        verify(chatRoomRepository, times(2)).save(any(ChatRoom.class));
    }

    @Test
    void createChatId() {
        // Arrange
        String senderId = "senderId";
        String recipientId = "recipientId";
        String expectedChatId = senderId + "_" + recipientId;

        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String chatId = chatRoomService.createChatId(senderId, recipientId);

        // Assert
        assertNotNull(chatId);
        assertEquals(expectedChatId, chatId);
        verify(chatRoomRepository, times(2)).save(any(ChatRoom.class));
    }
}
