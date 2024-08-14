package com.multi.udong.cs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRequest {
    private String version;
    private String userId;
    private long timestamp;
    private List<Bubble> bubbles;
    private String event;

    private String message;
}
