package com.multi.udong.notification.controller;

import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.notification.model.dto.NotiDTO;
import com.multi.udong.notification.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /**
     * Get unread noti response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @GetMapping("/getNoti")
    public ResponseEntity<List<NotiDTO>> getNoti(@AuthenticationPrincipal CustomUserDetails c) {
        int memberNo = c.getMemberDTO().getMemberNo();
        return ResponseEntity.ok(notiService.getNoti(memberNo));
    }

    /**
     * Get unread noti count response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @GetMapping("/getUnreadNotiCount")
    public ResponseEntity<Map<String, Integer>> getUnreadNotiCount(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        int count = notiService.getUnreadNotiCount(receiverNo);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Mark as read response entity.
     *
     * @param c      the c
     * @param notiNo the noti no
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/markAsRead")
    public ResponseEntity<Map<String, Boolean>> markAsRead(@AuthenticationPrincipal CustomUserDetails c,
                                                           @RequestParam("notiNo") Integer notiNo) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        boolean success = notiService.markAsRead(receiverNo, notiNo);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * Mark all as read response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/markAllAsReadNoti")
    public ResponseEntity<Map<String, Boolean>> markAllAsReadNoti(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        boolean success = notiService.markAllAsRead(receiverNo);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * Delete all read noti response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/deleteAllReadNoti")
    public ResponseEntity<Map<String, Boolean>> deleteAllReadNoti(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        boolean success = notiService.deleteAllReadNoti(receiverNo);
        return ResponseEntity.ok(Map.of("success", success));
    }
}
