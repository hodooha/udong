package com.multi.udong.share.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShaItemResultDTO {

    private List<ShaItemDTO> itemList;
    private int totalCounts;

}
