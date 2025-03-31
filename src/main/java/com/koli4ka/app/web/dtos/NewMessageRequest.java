package com.koli4ka.app.web.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class NewMessageRequest {
    private UUID senderId;
    private UUID receiverId;
    private String message;


}
