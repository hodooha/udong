package com.multi.udong.share.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ShaEvalDTO {

    private int evalNo;
    private int reqNo;
    private int reqItem;
    private int evrNo;
    private int eveNo;
    private int rating;
    private int totalScore;
    private LocalDate createdAt;

}
