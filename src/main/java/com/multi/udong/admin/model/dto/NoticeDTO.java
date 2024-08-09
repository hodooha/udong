package com.multi.udong.admin.model.dto;

import java.time.LocalDateTime;

public class NoticeDTO {
    private Integer noticeNo;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String title;
    private String content;
    private Integer writer;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean Popup;

    public Integer getNoticeNo() {
        return noticeNo;
    }

    public void setNoticeNo(Integer noticeNo) {
        this.noticeNo = noticeNo;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getWriter() {
        return writer;
    }

    public void setWriter(Integer writer) {
        this.writer = writer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Boolean getPopup() {
        return Popup;
    }

    public void setPopup(Boolean popup) {
        Popup = popup;
    }

    @Override
    public String toString() {
        return "NoticeDTO{" +
                "noticeNo=" + noticeNo +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", writer=" + writer +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                ", Popup=" + Popup +
                '}';
    }
}