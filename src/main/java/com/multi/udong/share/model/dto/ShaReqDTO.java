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
    private ShaItemDTO itemDTO;
    private int ownerNo;
    private String ownerNickname;
    private String reqGroup;
    private int rqstNo;
    private String rqstNickname;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDate returnDate;
    private String statusCode;
    private String statusName;
}
