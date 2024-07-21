package com.multi.udong.share.model.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class ShaItemDTO {
    private int itemNo;
    private String itemGroup;
    private String itemCatCode;
    private String title;
    private String content;
    private int ownerNo;
    private int locCode;
    private Date createdAt;
    private Date modifiedAt;
    private Date expiryDate;
    private String statusCode;
    private int likeCnt;
    private int reqCnt;
    private Date deletedAt;
}
