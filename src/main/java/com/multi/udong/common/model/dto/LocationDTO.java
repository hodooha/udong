package com.multi.udong.common.model.dto;

import lombok.Data;

/**
 * 지역(동네) DTO
 *
 * @author 강성현
 * @since 2024 -07-25
 */
@Data
public class LocationDTO {

    private long locationCode;
    private String siDoName;
    private String siGunGuName;
    private String eupMyeonDongName;

}
