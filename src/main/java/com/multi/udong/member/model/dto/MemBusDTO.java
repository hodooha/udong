package com.multi.udong.member.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemBusDTO {
    private Integer memberNo;
    private String businessNumber;
    private String representativeName;
    private String openingDate;
    private String createdAt;
    private Character approveStatus;
    private String approvedAt;

    private MemberDTO memberDTO;
    private AttachmentDTO attachmentDTO;
}