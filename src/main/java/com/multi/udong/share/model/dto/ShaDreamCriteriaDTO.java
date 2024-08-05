package com.multi.udong.share.model.dto;

import lombok.Data;

@Data
public class ShaDreamCriteriaDTO {

    private int itemNo;
    private int ownerNo;
    private int rqstNo;
    private String catCode;
    private String group;
    private String statusCode;
    private String keyword;
    private int page;
    private int start;
    private int end;

    public void setPageRange(int page){
        this.start = ShaDreamPageDTO.ITEM_COUNTS_FOR_PAGE * (page - 1) + 1;
        this.end = page * ShaDreamPageDTO.ITEM_COUNTS_FOR_PAGE;
    }

}
