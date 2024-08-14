package com.multi.udong.notification.service;

import com.multi.udong.notification.model.dto.NotiDTO;

import java.util.List;

/**
 * The interface Noti service.
 *
 * @author 김재식
 * @since 2024 -08-13
 */
public interface NotiService {
    List<NotiDTO> getNoti(Integer receiverNo);

    boolean markAsRead(Integer receiverNo, Integer notiNo);

    boolean markAllAsRead(Integer receiverNo);

    boolean deleteAllReadNoti(int receiverNo);

    int getUnreadNotiCount(int receiverNo);
}
