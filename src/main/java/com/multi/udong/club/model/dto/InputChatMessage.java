package com.multi.udong.club.model.dto;

import lombok.Data;

@Data
public class InputChatMessage {

    private int clubNo;
    private int senderNo;
    private String sender;
    private String content;
    private String type;

}
