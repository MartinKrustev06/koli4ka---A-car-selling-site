package com.koli4ka.app.message.client;

import com.koli4ka.app.message.dto.MessageRequest;
import com.koli4ka.app.message.dto.MessageResponse;
import com.koli4ka.app.web.dtos.NewMessageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageClient messageClient;

    public MessageService(MessageClient messageClient) {
        this.messageClient = messageClient;
    }


    public void saveMessage( NewMessageRequest newMessageRequest) {
        MessageRequest messageRequest= MessageRequest.builder()
                .message(newMessageRequest.getMessage())
                .receiverId(newMessageRequest.getReceiverId())
                .senderId(newMessageRequest.getSenderId())
                .build();
        messageClient.saveMessage(messageRequest);
    }

    // Получава чата между двама потребители
    public List<MessageResponse> getChat(UUID senderId, UUID receiverId) {
        return messageClient.getChat(senderId, receiverId);  // Извиква FeignClient
    }


    public List<UUID> getMessagesWithUser(UUID senderId) {

        List<UUID> uuids = messageClient.getChats(senderId);
        uuids.remove(senderId);
        System.out.println(uuids);
        return uuids;



    }
}
