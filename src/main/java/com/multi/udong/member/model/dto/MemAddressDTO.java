package com.multi.udong.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Mem address dto.
 *
 * @author : 재식
 * @since : 24. 7. 23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemAddressDTO {
    private Integer memberNo;
    private String siDoName;
    private String siGunGuName;
    private String eupMyeonDongName;
    private String detailAddress;
    private Long locationCode;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
