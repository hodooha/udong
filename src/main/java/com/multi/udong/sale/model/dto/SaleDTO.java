package com.multi.udong.sale.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;

import java.time.LocalDateTime;
import java.util.List;

public class SaleDTO {
    private int saleNo;
    private int writer;
    private String title;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int originalPrice;
    private int salePrice;
    private String address;
    private String content;
    private long locationCode;
    private String status;
    private String img;
    private List<AttachmentDTO> attachments;
    private String imagePath;

    public int getSaleNo() {
        return saleNo;
    }

    public void setSaleNo(int saleNo) {
        this.saleNo = saleNo;
    }

    public int getWriter() {
        return writer;
    }

    public void setWriter(int writer) {
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(long locationCode) {
        this.locationCode = locationCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "SaleDTO{" +
                "saleNo=" + saleNo +
                ", writer=" + writer +
                ", title='" + title + '\'' +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", originalPrice=" + originalPrice +
                ", salePrice=" + salePrice +
                ", address='" + address + '\'' +
                ", content='" + content + '\'' +
                ", locationCode=" + locationCode +
                ", status='" + status + '\'' +
                ", img='" + img + '\'' +
                ", attachments=" + attachments +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
