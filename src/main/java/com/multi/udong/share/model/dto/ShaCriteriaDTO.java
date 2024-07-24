package com.multi.udong.share.model.dto;

import lombok.Data;

/**
 * 대여 및 나눔 물건 검색 조건을 담은 dto
 *
 * @author 하지은
 * @since 2024 -07-24
 */
@Data
public class ShaCriteriaDTO {

    private String catCode;
    private String keyword;
    private String group;
    private Long locCode;
    private String statusCode;

}
