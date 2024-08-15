package com.multi.udong.club.model.dto;

import lombok.Data;

/**
 * 모임 조회용 필터(페이지, 검색) DTO
 *
 * @author 강성현
 * @since 2024 -07-25
 */
@Data
public class FilterDTO {

    private int page = 1;
    private int start;
    private int pageCount = 5; // 5개씩
    private int albumPageCount = 10; // 앨범은 10개씩
    private int memberPageCount = 15; // 멤버는 15명씩
    private int startIndex;
    private long locationCode;
    private String categoryCode;
    private String searchWord;
    private int clubNo;

    /**
     * 받아온 page 값으로 조회를 시작할 index 설정
     *
     * @param page the page
     */
    public void setStartAndStartIndex(int page) {

        start = 1 + (page - 1) * pageCount;
        // 1page: 1 + 0 * 5 => start 1
        // 2page: 1 + 1 * 5 => start 6

        startIndex = start - 1;
        // 1page: startIndex 0
        // 2page: startIndex 5

    }

    public void setAlbumStartAndStartIndex(int page) {

        start = 1 + (page - 1) * albumPageCount;
        // 1page: 1 + 0 * 10 => start 1
        // 2page: 1 + 1 * 10 => start 11

        startIndex = start - 1;
        // 1page: startIndex 0
        // 2page: startIndex 10

    }

    public void setMemberStartAndStartIndex(int page) {

        start = 1 + (page - 1) * memberPageCount;
        // 1page: 1 + 0 * 15 => start 1
        // 2page: 1 + 1 * 15 => start 16

        startIndex = start - 1;
        // 1page: startIndex 0
        // 2page: startIndex 15

    }

}
