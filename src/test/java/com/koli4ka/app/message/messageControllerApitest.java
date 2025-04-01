package com.koli4ka.app.message;

import com.koli4ka.app.message.client.MessageService;
import com.koli4ka.app.message.dto.MessageResponse;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.MessageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(MessageController.class)
class MessageControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserService userService;

    private UUID senderId;
    private UUID receiverId;
    private List<MessageResponse> messages;
    private User testUser;
    private User chatUser;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setId(senderId);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);
        
        chatUser = new User();
        chatUser.setId(receiverId);
        chatUser.setUsername("chatuser");
        chatUser.setRole(UserRole.USER);
        
        messages = Arrays.asList(
            createMessageResponse(senderId, receiverId, "Hello"),
            createMessageResponse(receiverId, senderId, "Hi there")
        );

        when(messageService.getChat(any(), any())).thenReturn(messages);
        when(messageService.getMessagesWithUser(any())).thenReturn(Arrays.asList(receiverId));
        when(userService.getById(any())).thenReturn(testUser);
        when(userService.getChatInfo(any())).thenReturn(Arrays.asList(chatUser));
    }

    @Test
    void testGetMessages() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/chat/getMessages/{senderId}/{receiverId}", senderId, receiverId)
                .with(SecurityMockMvcRequestPostProcessors.user(new AuthenticationDetails(senderId, "testuser", "password", UserRole.USER))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("chat"))
                .andExpect(MockMvcResultMatchers.model().attribute("chatMessages", messages))
                .andExpect(MockMvcResultMatchers.model().attribute("user", testUser));
    }

    @Test
    void testSendMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/chat/send-message")
                .with(SecurityMockMvcRequestPostProcessors.user(new AuthenticationDetails(senderId, "testuser", "password", UserRole.USER)))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("senderId", senderId.toString())
                .param("receiverId", receiverId.toString())
                .param("message", "Test message"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/chat/getMessages/" + senderId + "/" + receiverId));
    }

    @Test
    void testGetChats() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/chats")
                .with(SecurityMockMvcRequestPostProcessors.user(new AuthenticationDetails(senderId, "testuser", "password", UserRole.USER))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("chats"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", testUser))
                .andExpect(MockMvcResultMatchers.model().attribute("users", Arrays.asList(chatUser)));
    }

    private MessageResponse createMessageResponse(UUID senderId, UUID receiverId, String message) {
        MessageResponse response = new MessageResponse();
        response.setSenderId(senderId);
        response.setReceiverId(receiverId);
        response.setMessage(message);
        return response;
    }
}