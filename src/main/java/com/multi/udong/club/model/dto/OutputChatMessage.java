package com.multi.udong.club.model.dto;

import lombok.Data;

@Data
public class OutputChatMessage {

    private int senderNo;
    private String sender;
    private String content;
    private String sentAt;
    private String type;
    private String profileSavedName;

}
