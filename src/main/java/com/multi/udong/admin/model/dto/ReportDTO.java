package com.multi.udong.admin.model.dto;

public class ReportDTO {
    private int reportNo;
    private int reportedNo;  // 신고된 게시글 번호
    private String typeCode;
    private int reportedMember;
    private int reporterMember;
    private String reason;
    private String url;
    private String status;

    public int getReportNo() {
        return reportNo;
    }

    public void setReportNo(int reportNo) {
        this.reportNo = reportNo;
    }

    public int getReportedNo() {
        return reportedNo;
    }

    public void setReportedNo(int reportedNo) {
        this.reportedNo = reportedNo;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public int getReportedMember() {
        return reportedMember;
    }

    public void setReportedMember(int reportedMember) {
        this.reportedMember = reportedMember;
    }

    public int getReporterMember() {
        return reporterMember;
    }

    public void setReporterMember(int reporterMember) {
        this.reporterMember = reporterMember;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReportDTO{" +
                "reportNo=" + reportNo +
                ", reportedNo=" + reportedNo +
                ", typeCode='" + typeCode + '\'' +
                ", reportedMember=" + reportedMember +
                ", reporterMember=" + reporterMember +
                ", reason='" + reason + '\'' +
                ", url='" + url + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}