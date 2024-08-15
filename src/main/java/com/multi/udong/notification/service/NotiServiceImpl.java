package com.multi.udong.notification.service;

import com.multi.udong.notification.model.dao.NotiMapper;
import com.multi.udong.notification.model.dto.NotiDTO;
import com.multi.udong.notification.model.dto.NotiSetCodeENUM;
import com.multi.udong.notification.model.dto.NotiSetDTO;
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
 * @since 2024 -08-15
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotiServiceImpl implements NotiService{
    private final NotiMapper notiMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 알림 전송
     *
     * @param type        the type
     * @param receiverNo  the receiver no
     * @param receiverNos the receiver nos
     * @param targetNo    the target no
     * @param params      the params
     * @since 2024 -08-15
     */
    public void sendNoti(NotiSetCodeENUM type,
                         @RequestParam("receiverNo") Integer receiverNo,
                         List<Integer> receiverNos,
                         @RequestParam(value = "targetNo", required = false) Integer targetNo,
                         Map<String, String> params) {

        // 알림 내용 전처리
        String content = null;
        for (String key : params.keySet()) {
            content = type.getMessageTemplate().replace("{" + key + "}", params.get(key));
        }

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
            // 여러 개의 알림을 한 번에 insert
            notiMapper.insertNoti(notiDTOs);

            // 날짜를 LocalDateTime 에서  String 으로 포매팅
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            for (NotiDTO notiDTO : notiDTOs) {
                notiDTO.setFormatCreatedAt(formattedDateTime);
            }

            // 실제로 insert 된 알림만 가져오기
            List<NotiDTO> insertedNotis = notiMapper.getInsertedNotis(notiDTOs);

            // 수신자들에게 웹소켓으로 알림 전송
            for (NotiDTO notiDTO : insertedNotis) {
                notiDTO.setFormatCreatedAt(formattedDateTime);
                simpMessagingTemplate.convertAndSend("/topic/noti/" + notiDTO.getReceiverNo(), notiDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<NotiDTO> getNoti(Integer receiverNo) {
        return notiMapper.getNoti(receiverNo);
    }

    @Override
    public boolean markAsRead(Integer receiverNo, Integer notiNo) {
        return notiMapper.markAsRead(receiverNo, notiNo) > 0;
    }

    @Override
    public boolean markAllAsRead(Integer receiverNo) {
        return notiMapper.markAllAsRead(receiverNo) > 0;
    }

    @Override
    public boolean deleteAllReadNoti(int receiverNo) {
        return notiMapper.deleteAllReadNoti(receiverNo) > 0;
    }

    @Override
    public int getUnreadNotiCount(int receiverNo) {
        return notiMapper.getUnreadNotiCount(receiverNo);
    }

    @Override
    public List<NotiSetDTO> getNotiSetByMemberNo(int memberNo) {
        return notiMapper.getNotiSetByMemberNo(memberNo);
    }

    @Override
    public boolean updateNotiSet(int memberNo, Map<String, String> params) {
        return notiMapper.updateNotiSet(memberNo, params) > 0;
    }
}
