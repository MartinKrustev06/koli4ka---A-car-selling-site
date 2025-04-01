package com.koli4ka.app.message;

import com.koli4ka.app.message.client.MessageService;
import com.koli4ka.app.message.dto.MessageResponse;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.MessageController;
import com.koli4ka.app.web.dtos.NewMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserService userService;

    private User testUser;
    private AuthenticationDetails authDetails;
    private UUID userId;
    private UUID otherUserId;
    private MessageResponse testMessage;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        authDetails = new AuthenticationDetails(userId, "testuser", "password", UserRole.USER);

        testMessage = new MessageResponse();
        testMessage.setSenderId(userId);
        testMessage.setReceiverId(otherUserId);
        testMessage.setMessage("Test message");
    }

    @Test
    void testGetMessages() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);
        when(messageService.getChat(any(UUID.class), any(UUID.class)))
                .thenReturn(Arrays.asList(testMessage));

        mockMvc.perform(get("/chat/getMessages/{senderId}/{receiverId}", userId, otherUserId)
                .with(user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attributeExists("chatMessages"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testSendMessage() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);

        NewMessageRequest request = new NewMessageRequest();
        request.setReceiverId(otherUserId);
        request.setMessage("Test message");
        request.setSenderId(userId);

        mockMvc.perform(post("/chat/send-message")
                .with(user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("newMessageRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat/getMessages/" + userId + "/" + otherUserId));
    }

    @Test
    void testGetChats() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);
        when(messageService.getMessagesWithUser(any(UUID.class)))
                .thenReturn(Arrays.asList(otherUserId));
        when(userService.getChatInfo(any())).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/chats")
                .with(user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("chats"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void allEndpoints_WhenNotAuthenticated_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/chats"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/chat/getMessages/{senderId}/{receiverId}", UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/chat/send-message")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
} 