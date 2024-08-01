package com.multi.udong.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private Integer memberNo;
    private Integer start;
    private Integer end;
    private Integer page;

    private String searchCategory;
    private String searchWord;

    public void setStartEnd (int page) {
        start = 1 + (page - 1) * 10;
        end = page * 10;
    }
}
