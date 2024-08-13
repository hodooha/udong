package com.multi.udong.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    public Integer targetNo;
    public String notiType;
    public String content;
    public String createdAt;
    public Character isRead;
}
