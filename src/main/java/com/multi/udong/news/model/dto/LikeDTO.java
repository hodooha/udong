package com.multi.udong.news.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDTO {

    private int memberNo;
    private int newsNo;
    private int replyNo;
    private LocalDateTime likedAt;

}
