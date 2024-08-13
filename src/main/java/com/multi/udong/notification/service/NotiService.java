package com.multi.udong.notification.service;

import com.multi.udong.notification.model.dto.NotiDTO;

import java.util.List;

public interface NotiService {
    void createNoti(NotiDTO notiDTO);

    List<NotiDTO> getUnreadNoti(Integer receiverNo);

    void markAsRead(Integer notiNo);

    boolean markAllAsRead(Integer receiverNo);
}
