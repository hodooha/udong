package com.multi.udong.news.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewsDTO {

    private int newsNo;
    private CategoryDTO category;
    private LocationDTO location;
    private String title;
    private String content;
    private MemberDTO writer;
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
