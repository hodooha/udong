package com.multi.udong.club.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OutputChatMessage {

    private int chatNo;
    private int clubNo;
    private int senderNo;
    private String sender;
    private String content;
    private LocalDateTime sentAt;
    private String sentAtStr;
    private String type;
    private String profileSavedName;

}
