package com.multi.udong.share.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 대여 및 나눔 물건 검색 조건을 담은 dto
 *
 * @author 하지은
 * @since 2024 -07-24
 */
@Data
public class ShaCriteriaDTO {

    private String catCode;
    private List<ShaCatDTO> catList;
    private String keyword;
    private String group;
    private Long locCode;
    private String statusCode;
    private int page;
    private int start;
    private int end;

    public void setPageRange(int page){
        this.start = ShaPageDTO.ITEM_COUNTS_FOR_PAGE * (page - 1) + 1;
        this.end = page * ShaPageDTO.ITEM_COUNTS_FOR_PAGE;
    }

}
