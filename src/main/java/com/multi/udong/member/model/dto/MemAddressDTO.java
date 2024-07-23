package com.multi.udong.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 재식
 * @since : 24. 7. 23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemAddressDTO {
    private Integer memberNo;
    private String address;
    private String detailedAddress;
    private Long locationCode;
    private String createdAt;
    private String modifiedAt;
}
