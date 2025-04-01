package com.koli4ka.app.message;

import com.koli4ka.app.message.client.MessageClient;
import com.koli4ka.app.message.client.MessageService;
import com.koli4ka.app.message.dto.MessageRequest;
import com.koli4ka.app.message.dto.MessageResponse;
import com.koli4ka.app.web.dtos.NewMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageClient messageClient;

    private MessageService messageService;

    private UUID senderId;
    private UUID receiverId;
    private NewMessageRequest newMessageRequest;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageClient);
        
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        
        newMessageRequest = new NewMessageRequest();
        newMessageRequest.setMessage("Hello!");
        newMessageRequest.setSenderId(senderId);
        newMessageRequest.setReceiverId(receiverId);

        messageResponse = new MessageResponse();
        messageResponse.setMessage("Hello!");
        messageResponse.setSenderId(senderId);
        messageResponse.setReceiverId(receiverId);
    }

    @Test
    void saveMessage_ShouldCallMessageClient() {
        // Act
        messageService.saveMessage(newMessageRequest);

        // Assert
        verify(messageClient).saveMessage(any(MessageRequest.class));
    }

    @Test
    void getChat_ShouldReturnMessagesFromClient() {
        // Arrange
        List<MessageResponse> expectedMessages = new ArrayList<>();
        expectedMessages.add(messageResponse);
        when(messageClient.getChat(senderId, receiverId)).thenReturn(expectedMessages);

        // Act
        List<MessageResponse> actualMessages = messageService.getChat(senderId, receiverId);

        // Assert
        assertEquals(expectedMessages, actualMessages);
        verify(messageClient).getChat(senderId, receiverId);
    }

    @Test
    void getMessagesWithUser_ShouldReturnUniqueUserIds() {
        // Arrange
        List<UUID> allChats = new ArrayList<>();
        allChats.add(senderId);
        allChats.add(receiverId);
        allChats.add(UUID.randomUUID());
        when(messageClient.getChats(senderId)).thenReturn(allChats);

        // Act
        List<UUID> actualUsers = messageService.getMessagesWithUser(senderId);

        // Assert
        assertEquals(2, actualUsers.size());
        assertFalse(actualUsers.contains(senderId));
        assertTrue(actualUsers.contains(receiverId));
        verify(messageClient).getChats(senderId);
    }

    @Test
    void getMessagesWithUser_ShouldReturnEmptyList_WhenNoOtherUsers() {
        // Arrange
        List<UUID> allChats = new ArrayList<>();
        allChats.add(senderId);
        when(messageClient.getChats(senderId)).thenReturn(allChats);

        // Act
        List<UUID> actualUsers = messageService.getMessagesWithUser(senderId);

        // Assert
        assertTrue(actualUsers.isEmpty());
        verify(messageClient).getChats(senderId);
    }
} 