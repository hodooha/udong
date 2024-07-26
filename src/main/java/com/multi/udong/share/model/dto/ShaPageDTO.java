package com.multi.udong.share.model.dto;

import lombok.Data;

@Data
public class ShaPageDTO {

    public static final int PAGE_GROUP = 3;
    public static final int ITEM_COUNTS_FOR_PAGE = 8;

    private int currentPage;
    private int currentGroup;
    private int startPage;
    private int endPage;
    private int totalPage;
    private int totalCounts;

    public void setPageInfo(int totalCounts){
        this.totalCounts = totalCounts;
        this.totalPage = (int)Math.ceil((double)totalCounts/ITEM_COUNTS_FOR_PAGE);
        this.currentGroup = (int)Math.ceil((double) currentPage /PAGE_GROUP);
        this.endPage = Math.min(currentGroup * PAGE_GROUP, totalPage);
        this.startPage = currentGroup * PAGE_GROUP - (PAGE_GROUP -1);
    }
}
