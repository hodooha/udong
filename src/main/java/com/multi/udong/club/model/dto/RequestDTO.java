package com.multi.udong.club.model.dto;

import lombok.Data;

/**
 * 요청용 DTO
 *
 * @author 강성현
 * @since 2024 -07-25
 */
@Data
public class RequestDTO {

    private int memberNo;
    private int clubNo;
    private int logNo;
    private int replyNo;

}
