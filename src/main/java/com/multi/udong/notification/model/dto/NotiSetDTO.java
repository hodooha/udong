package com.multi.udong.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotiSetDTO {
    public Integer memberNo;
    public Integer notiSetCode;
    public Character isReceived;
}
