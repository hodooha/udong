package com.multi.udong.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Noti dto.
 *
 * @author 김재식
 * @since 2024 -08-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotiDTO {
    public Integer notiNo;
    public Integer receiverNo;
    public String targetHref;
    public String notiSetCode;
    public String content;
    public LocalDateTime createdAt;
    public Character isRead;

    public String formatCreateAt;
}
