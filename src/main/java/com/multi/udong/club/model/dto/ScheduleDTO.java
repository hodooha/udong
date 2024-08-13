package com.multi.udong.club.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleDTO {

    private int scheduleNo;
    private int clubNo;
    private String title;
    private String content;
    private ClubMemberDTO maker;
    private int currentPersonnel;
    private int maxPersonnel;
    private LocalDateTime gatheringAt;
    private String gatheringAtStr;
    private LocalDateTime createdAt;
    private String isDeleted;
    private LocalDateTime deletedAt;
    private List<ClubMemberDTO> participants;
    private int viewerJoin;

}
