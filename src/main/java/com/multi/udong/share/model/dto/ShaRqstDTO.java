package com.multi.udong.share.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShaRqstDTO {

    private int reqNo;
    private int reqItem;
    private int rqstNo;
    private String rqstNickname;
    private String rqstImg;
    private int rqstLevel;
    private int endReqCnt;
    private float avgRating;
    private LocalDate returnDate;
    private LocalDateTime reqCreatedAt;

}
