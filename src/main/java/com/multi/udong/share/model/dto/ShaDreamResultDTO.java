package com.multi.udong.share.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShaDreamResultDTO {

    private List<ShaReqDTO> borrowList;
    private List<ShaItemDTO> lendList;
    private int totalCounts;

}
