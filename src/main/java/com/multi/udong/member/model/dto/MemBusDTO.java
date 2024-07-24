package com.multi.udong.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemBusDTO {
    private Integer memberNo;
    private String businessNumber;
    private String isCorporate;
    private String companyName;
    private String representativeName;
    private String openingDate;
    private String businessLocation;
    private String businessType;
    private String businessItem;
    private String issueDate;
    private String createdAt;
    private Character approveStatus;
    private String approvedAt;
}