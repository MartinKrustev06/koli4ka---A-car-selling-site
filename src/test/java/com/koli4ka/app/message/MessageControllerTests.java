package com.koli4ka.app.message;

import com.koli4ka.app.message.client.MessageService;
import com.koli4ka.app.message.dto.MessageResponse;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.MessageController;
import com.koli4ka.app.web.dtos.NewMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MessageController.class)
class messagecontrollertest {

    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private MessageController messageController;

    private UUID senderId;
    private UUID receiverId;
    private User senderUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        senderUser = new User();
        senderUser.setId(senderId);
        senderUser.setUsername("testUser");

        // Конфигуриране на MockMvc с Thymeleaf support
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @WithMockUser
    void testGetMessages() throws Exception {
        MessageResponse message1 = new MessageResponse();
        message1.setMessage("Hello");
        message1.setSenderId(senderId);
        message1.setReceiverId(receiverId);

        List<MessageResponse> mockMessages = List.of(message1);
        when(messageService.getChat(senderId, receiverId)).thenReturn(mockMessages);
        when(userService.getById(senderId)).thenReturn(senderUser);

        mockMvc.perform(get("/chat/getMessages/{senderId}/{receiverId}", senderId, receiverId))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attributeExists("user", "senderId", "receiverId", "chatMessages", "newMessageRequest"));

        verify(messageService, times(1)).getChat(senderId, receiverId);
        verify(userService, times(1)).getById(senderId);
    }

    @Test
    @WithMockUser
    void testSendMessage() throws Exception {
        NewMessageRequest request = new NewMessageRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setMessage("Test Message");

        mockMvc.perform(post("/chat/send-message")
                        .param("senderId", senderId.toString())
                        .param("receiverId", receiverId.toString())
                        .param("content", "Test Message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat/getMessages/" + senderId + "/" + receiverId));

        verify(messageService, times(1)).saveMessage(any(NewMessageRequest.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetChats() throws Exception {
        AuthenticationDetails authDetails = mock(AuthenticationDetails.class);
        when(authDetails.getUserId()).thenReturn(senderId);
        when(userService.getById(senderId)).thenReturn(senderUser);
        when(messageService.getMessagesWithUser(senderId)).thenReturn(List.of(receiverId));
        when(userService.getChatInfo(List.of(receiverId))).thenReturn(List.of(new User()));

        mockMvc.perform(get("/chats"))
                .andExpect(status().isOk())
                .andExpect(view().name("chats"))
                .andExpect(model().attributeExists("user", "users"));

        verify(messageService, times(1)).getMessagesWithUser(senderId);
        verify(userService, times(1)).getChatInfo(List.of(receiverId));
    }
}
