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
    private String typeName;
    private String reportedMemberId;
    private String reporterMemberId;
    private String imageFileName;

    public void setUrl(String url) {
        this.url = url;
        if (url != null && url.contains("?file=")) {
            this.imageFileName = url.substring(url.indexOf("?file=") + 6);
        }
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getReportedMemberId() {
        return reportedMemberId;
    }

    public void setReportedMemberId(String reportedMemberId) {
        this.reportedMemberId = reportedMemberId;
    }

    public String getReporterMemberId() {
        return reporterMemberId;
    }

    public void setReporterMemberId(String reporterMemberId) {
        this.reporterMemberId = reporterMemberId;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
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
                ", typeName='" + typeName + '\'' +
                ", reportedMemberId='" + reportedMemberId + '\'' +
                ", reporterMemberId='" + reporterMemberId + '\'' +
                '}';
    }


}