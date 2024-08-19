package com.multi.udong.cs.model.dto;

import lombok.Data;

@Data
public class ChatbotHistory {
    private int historyNo;
    private int memberNo;
    private String message;
    private String sender;
    private String createdAt;
}
