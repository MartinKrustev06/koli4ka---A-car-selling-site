package com.koli4ka.app.message.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MessageRequest {

    private UUID senderId;
    private UUID receiverId;
    private String message;

}
