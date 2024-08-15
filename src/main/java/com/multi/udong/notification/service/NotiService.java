package com.multi.udong.notification.service;

import com.multi.udong.notification.model.dto.NotiDTO;
import com.multi.udong.notification.model.dto.NotiSetDTO;

import java.util.List;
import java.util.Map;

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

    List<NotiSetDTO> getNotiSetByMemberNo(int memberNo);

    boolean updateNotiSet(int memberNo, Map<String, String> params);
}
