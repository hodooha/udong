package com.multi.udong.message.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Message dto.
 *
 * @author 김재식
 * @since 2024 -08-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Integer messageNo;
    private Integer senderNo;
    private Integer receiverNo;
    private String content;
    private String createdAt;
    private Character isRead;

    private String senderNickname;
    private String receiverNickname;
    private Integer totalCount;
}
