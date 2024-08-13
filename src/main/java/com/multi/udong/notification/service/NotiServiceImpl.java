package com.multi.udong.notification.service;

import com.multi.udong.notification.model.dao.NotiMapper;
import com.multi.udong.notification.model.dto.NotiDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Noti service.
 *
 * @author 김재식
 * @since 2024 -08-13
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotiServiceImpl implements NotiService{
    private final NotiMapper notiMapper;

    /**
     * Create noti.
     *
     * @param notiDTO the noti dto
     * @since 2024 -08-13
     */
    public void createNoti(NotiDTO notiDTO) {
        notiMapper.insertNoti(notiDTO);
    }

    /**
     * Get unread noti list.
     *
     * @param receiverNo the receiver no
     * @return the list
     * @since 2024 -08-13
     */
    @Override
    public List<NotiDTO> getUnreadNoti(Integer receiverNo) {
        return notiMapper.getUnreadNoti(receiverNo);
    }

    /**
     * Mark as read.
     *
     * @param notiNo the noti no
     * @since 2024 -08-13
     */
    @Override
    public void markAsRead(Integer notiNo) {
        notiMapper.markAsRead(notiNo);
    }

    /**
     * Mark all as read boolean.
     *
     * @param receiverNo the receiver no
     * @return the boolean
     * @since 2024 -08-13
     */
    @Override
    public boolean markAllAsRead(Integer receiverNo) {
        return true;
    }
}
