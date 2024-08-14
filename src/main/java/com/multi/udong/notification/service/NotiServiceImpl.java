package com.multi.udong.notification.service;

import com.multi.udong.notification.model.dao.NotiMapper;
import com.multi.udong.notification.model.dto.NotiDTO;
import com.multi.udong.notification.model.dto.NotiSetCodeENUM;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Create noti.
     *
     * @param type        the type
     * @param receiverNo  the receiver no
     * @param receiverNos the receiver nos
     * @param targetNo    the target no
     * @param params      the params
     * @since 2024 -08-13
     */
    public void createNoti(NotiSetCodeENUM type,
                           @RequestParam("receiverNo") Integer receiverNo,
                           List<Integer> receiverNos,
                           @RequestParam(value = "targetNo", required = false) Integer targetNo,
                           Map<String, String> params) {

        String content = formatMessage(type.getMessageTemplate(), params);

        // 웹소켓과 DB의 시간차이를 없애기 위해 서버에서 시간을 직접 입력
        LocalDateTime now = LocalDateTime.now();

        // 여러 개의 NotiDTO를 담을 리스트 생성
        List<NotiDTO> notiDTOs = new ArrayList<>();
        receiverNos.add(receiverNo);

        for (Integer receiver : receiverNos) {
            NotiDTO notiDTO = new NotiDTO();
            notiDTO.setReceiverNo(receiver);
            notiDTO.setTargetHref(type.getHref() + targetNo);
            notiDTO.setNotiSetCode(type.name());
            notiDTO.setContent(content);
            notiDTO.setCreatedAt(now);
            notiDTOs.add(notiDTO);
        }

        try {
            // 여러 개의 알림을 한 번에 삽입
            notiMapper.insertNoti(notiDTOs);

            // String으로 포매팅
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            // 각 수신자에게 웹소켓으로 알림 전송
            for (NotiDTO notiDTO : notiDTOs) {
                notiDTO.setFormatCreateAt(formattedDateTime);
                simpMessagingTemplate.convertAndSend("/topic/noti/" + notiDTO.getReceiverNo(), notiDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatMessage(String template, Map<String, String> params) {

        for (String key : params.keySet()) {
            template = template.replace("{" + key + "}", params.get(key));
        }

        return template;
    }

    /**
     * Get unread noti list.
     *
     * @param receiverNo the receiver no
     * @return the list
     * @since 2024 -08-13
     */
    @Override
    public List<NotiDTO> getNoti(Integer receiverNo) {
        return notiMapper.getNoti(receiverNo);
    }

    /**
     * Mark as read.
     *
     * @param receiverNo the receiver no
     * @param notiNo     the noti no
     * @return the boolean
     * @since 2024 -08-13
     */
    @Override
    public boolean markAsRead(Integer receiverNo, Integer notiNo) {
        return notiMapper.markAsRead(receiverNo, notiNo) > 0;
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
        return notiMapper.markAllAsRead(receiverNo) > 0;
    }

    /**
     * Delete all read noti boolean.
     *
     * @param receiverNo the receiver no
     * @return the boolean
     * @since 2024 -08-13
     */
    @Override
    public boolean deleteAllReadNoti(int receiverNo) {
        return notiMapper.deleteAllReadNoti(receiverNo) > 0;
    }

    /**
     * Get unread noti count int.
     *
     * @param receiverNo the receiver no
     * @return the int
     * @since 2024 -08-13
     */
    @Override
    public int getUnreadNotiCount(int receiverNo) {
        return notiMapper.getUnreadNotiCount(receiverNo);
    }
}
