package com.multi.udong.club.model.dto;

import lombok.Data;

@Data
public class ReportDTO {

    private int reportNo;
    private String typeCode;
    private int reportedNo;
    private int reportedMember;
    private int reporterMember;
    private String reason;
    private String url;
    private String status;

}
