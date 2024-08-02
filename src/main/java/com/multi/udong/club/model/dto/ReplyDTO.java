package com.multi.udong.club.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReplyDTO {

    private int replyNo;
    private int logNo;
    private String content;
    private ClubMemberDTO writer;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewerLike;
    private List<LikeDTO> likes;

}
