package com.multi.udong.share.model.dto;

import lombok.Data;

@Data
public class ShaReportDTO {

    private int reportNo;
    private String typeCode;
    private int reportedNo;
    private int reportedMember;
    private int reporterMember;
    private String reasonType;
    private String reasonDetail;
    private String reason;
    private String url;
    private String status;

    public void reasonConcat(){
        String totalReason = "[" + this.reasonType + "]" + this.reasonDetail;
        setReason(totalReason);

    }
}
