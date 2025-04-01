package com.koli4ka.app.message;

import com.koli4ka.app.web.MessageController;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.koli4ka.app.message.client.MessageService;
import com.koli4ka.app.message.dto.MessageResponse;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class MessageIntegrationTest {

    @Mock
    private MessageService messageService;
    @Mock
    private UserService userService;

    @InjectMocks
    private MessageController messageController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetMessages() throws Exception {
        // Мокваме методите върху мок обектите
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        List<MessageResponse> chatMessages = new ArrayList<>();
        chatMessages.add(new MessageResponse());

        User mockUser = new User();
        mockUser.setId(senderId);

        // Задаваме какво да се върне от мок методите
        when(messageService.getChat(senderId, receiverId)).thenReturn(chatMessages);
        when(userService.getById(senderId)).thenReturn(mockUser);

        // Извикваме тестовия метод
        mockMvc.perform(get("/chat/getMessages/{senderId}/{receiverId}", senderId, receiverId))
                .andExpect(status().is3xxRedirection());
        verify(messageService, times(0)).getChat(senderId, receiverId);
    }
}
