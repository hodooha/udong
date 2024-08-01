package com.multi.udong.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private int fileNo;
    private String typeCode;
    private int targetNo;
    private String originalName;
    private String savedName;

    private String fileUrl;

}
