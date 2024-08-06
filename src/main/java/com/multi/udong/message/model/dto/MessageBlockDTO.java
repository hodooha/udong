package com.multi.udong.message.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Message block dto.
 *
 * @author 김재식
 * @since 2024 -08-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageBlockDTO {
    private Integer blockerNo;
    private Integer blockedNo;
    private String createdAt;
}
