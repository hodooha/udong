package com.multi.udong.share.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class ShaItemDTO {
    private int itemNo;
    private String itemGroup;
    private String itemCatCode;
    private String title;
    private String content;
    private int ownerNo;
    private int locCode;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String statusCode;
    private int dealCnt;
    private int likeCnt;
    private int viewCnt;
    private int reqCnt;
    private Timestamp deletedAt;
    private String img;
    private List<AttachmentDTO> imgList;



}
