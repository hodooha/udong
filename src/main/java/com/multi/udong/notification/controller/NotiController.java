package com.multi.udong.notification.controller;

import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.message.model.dto.MessageDTO;
import com.multi.udong.notification.model.dto.NotiDTO;
import com.multi.udong.notification.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * The type Noti controller.
 *
 * @author 김재식
 * @since 2024 -08-13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noti")
public class NotiController {
    private final NotiService notiService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Get unread noti response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotiDTO>> getUnreadNoti(@AuthenticationPrincipal CustomUserDetails c) {
        int memberNo = c.getMemberDTO().getMemberNo();
        return ResponseEntity.ok(notiService.getUnreadNoti(memberNo));
    }

    /**
     * Mark as read response entity.
     *
     * @param notiNo the noti no
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/mark-read")
    public ResponseEntity<?> markAsRead(@RequestParam Integer notiNo) {
        notiService.markAsRead(notiNo);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all as read response entity.
     *
     * @param receiverNo the receiver no
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/mark-all-read")
    public ResponseEntity<Map<String, Boolean>> markAllAsRead(@RequestParam Integer receiverNo) {
        boolean success = notiService.markAllAsRead(receiverNo);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * Send noti.
     *
     * @since 2024 -08-13
     */
    public void SendNoti() {
        NotiDTO notiDTO = new NotiDTO();
        notiService.createNoti(notiDTO);
        simpMessagingTemplate.convertAndSend("/topic/noti/" + notiDTO.getReceiverNo(), notiDTO);
    }

    /**
     * New message noti.
     *
     * @param messageDTO the message dto
     * @since 2024 -08-13
     */
    public void newMessageNoti(MessageDTO messageDTO) {
        NotiDTO notiDTO = new NotiDTO();
        notiDTO.setReceiverNo(messageDTO.getReceiverNo());
        notiDTO.setNotiType("MEM");
        notiDTO.setContent("새 쪽지가 도착했습니다.");
        notiService.createNoti(notiDTO);
        simpMessagingTemplate.convertAndSend("/topic/noti/" + notiDTO.getReceiverNo(), notiDTO);
    }
}
