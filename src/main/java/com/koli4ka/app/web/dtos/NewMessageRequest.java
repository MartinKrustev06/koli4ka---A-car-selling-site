package com.koli4ka.app.web.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class NewMessageRequest {
    private UUID senderId;
    private UUID receiverId;
    private String message;

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
