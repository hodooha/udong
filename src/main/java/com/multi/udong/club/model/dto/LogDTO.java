package com.multi.udong.club.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LogDTO {

    private int logNo;
    private int clubNo;
    private String title;
    private String content;
    private ClubMemberDTO writer;
    private int views;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int repliesCount;
    private int likesCount;
    private int viewerLike;
    private List<AttachmentDTO> attachments;
    private List<LikeDTO> likes;
    private List<ReplyDTO> replies;
    private String isDeleted;
    private LocalDateTime deletedAt;


}
