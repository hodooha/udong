package com.multi.udong.common.model.dto;

import lombok.Data;

@Data
public class AttachmentDTO {
    private int fileNo;
    private String typeCode;
    private int targetNo;
    private String originalName;
    private String savedName;

}