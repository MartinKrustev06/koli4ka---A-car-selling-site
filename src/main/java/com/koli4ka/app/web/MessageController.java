package com.koli4ka.app.web;

import com.koli4ka.app.message.client.MessageService;
import com.koli4ka.app.message.dto.MessageResponse;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.NewMessageRequest;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    // Метод за вземане на съобщения
    @GetMapping("/chat/getMessages/{senderId}/{receiverId}")
    public ModelAndView getMessages(@PathVariable UUID senderId, @PathVariable UUID receiverId) {
        System.out.println("Sender ID: " + senderId);
        System.out.println("Receiver ID: " + receiverId);

        List<MessageResponse> chatMessages = messageService.getChat(senderId, receiverId);
        User user = userService.getById(senderId);

        NewMessageRequest newMessageRequest = new NewMessageRequest();
        newMessageRequest.setSenderId(senderId);
        newMessageRequest.setReceiverId(receiverId);

        ModelAndView modelAndView = new ModelAndView("chat");
        modelAndView.addObject("user", user);
        modelAndView.addObject("senderId", senderId);
        modelAndView.addObject("receiverId", receiverId);
        modelAndView.addObject("chatMessages", chatMessages);
        modelAndView.addObject("newMessageRequest", newMessageRequest); // Тук вече е попълнен DTO

        return modelAndView;
    }

    @PostMapping("/chat/send-message")
    public String sendMessage(@ModelAttribute NewMessageRequest newMessageRequest) {
        // Изпращаме съобщението чрез MessageService
        messageService.saveMessage(newMessageRequest);

        // Пренасочваме към чата
        return "redirect:/chat/getMessages/" + newMessageRequest.getSenderId() + "/" + newMessageRequest.getReceiverId();
    }
    @GetMapping("/chats")
    public ModelAndView getChats(@AuthenticationPrincipal AuthenticationDetails details) {
        UUID senderId= details.getUserId();
        User user=userService.getById(details.getUserId());

       List<UUID> uuids= messageService.getMessagesWithUser(senderId);
       List<User> users=userService.getChatInfo(uuids);
        ModelAndView mav= new ModelAndView("chats");
        mav.addObject("user", user);
        mav.addObject("users", users);

        return mav;

    }

}
