package com.multi.udong.share.model.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 대여 및 나눔 요청 DTO
 *
 * @author 하지은
 * @since 2024 -07-23
 */
@Data
public class ShaReqDTO {

    private int reqNo;
    private int reqItem;
    private int ownerNo;
    private String reqGroup;
    private int rqstNo;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDate returnDate;
    private String statusCode;
}
