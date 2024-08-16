package com.multi.udong.news.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReplyDTO {

    private int replyNo;
    private int newsNo;
    private String content;
    private MemberDTO writer;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewerLike;
    private int likesCount;
    private List<LikeDTO> likes;

}
