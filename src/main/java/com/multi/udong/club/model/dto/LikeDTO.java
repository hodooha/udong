package com.multi.udong.club.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDTO {

    private int memberNo;
    private int logNo;
    private int replyNo;
    private LocalDateTime likedAt;

}
