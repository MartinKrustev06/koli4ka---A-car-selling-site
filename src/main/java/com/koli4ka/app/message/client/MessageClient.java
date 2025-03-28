package com.koli4ka.app.message.client;

import com.koli4ka.app.message.dto.MessageRequest;
import com.koli4ka.app.message.dto.MessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "message-svc", url = "http://localhost:8000")
public interface MessageClient {

    @PostMapping("/api/v1/messages")
    void saveMessage(@RequestBody MessageRequest messageRequest);

    @GetMapping("/api/v1/messages/{senderId}/{receiverId}")
    List<MessageResponse> getChat(@PathVariable("senderId") UUID senderId, @PathVariable("receiverId") UUID receiverId);

    @GetMapping("/api/v1/messages/getChats/{senderId}")
    List<UUID> getChats(@PathVariable("senderId") UUID senderId);
}
