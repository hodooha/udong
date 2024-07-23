package com.multi.udong.club.model.dto;

import lombok.Data;

@Data
public class FilterDTO {

    private int page;
    private int start;
    private int pageCount = 5; // 5개씩
    private int startIndex;
    private long locationCode;
    private String categoryCode;
    private String searchWord;

    // start와 startIndex를 구하는 메소드
    public void setStartAndStartIndex(int page) {

        start = 1 + (page - 1) * pageCount;
        // 1page: 1 + 0 * 5 => start 1
        // 2page: 1 + 1 * 5 => start 6

        startIndex = start - 1;
        // 1page: startIndex 0
        // 2page: startIndex 5

    }

}
