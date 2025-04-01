package com.koli4ka.app.message.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageResponse {
    private UUID senderId;
    private UUID receiverId;
    private String message;


}
