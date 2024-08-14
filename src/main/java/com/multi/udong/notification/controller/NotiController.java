package com.multi.udong.notification.controller;

import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.notification.model.dto.NotiDTO;
import com.multi.udong.notification.model.dto.NotiSetDTO;
import com.multi.udong.notification.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Noti controller.
 *
 * @author 김재식
 * @since 2024 -08-13
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/noti")
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
    @ResponseBody
    public List<NotiDTO> getNoti(@AuthenticationPrincipal CustomUserDetails c) {
        int memberNo = c.getMemberDTO().getMemberNo();
        return notiService.getNoti(memberNo);
    }

    /**
     * Get unread noti count response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @GetMapping("/getUnreadNotiCount")
    @ResponseBody
    public int getUnreadNotiCount(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        return notiService.getUnreadNotiCount(receiverNo);
    }

    /**
     * Noti set.
     *
     * @param c     the c
     * @param model the model
     * @since 2024 -08-14
     */
    @GetMapping("/notiSet")
    public String notiSet(@AuthenticationPrincipal CustomUserDetails c, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        List<NotiSetDTO> notiSets = notiService.getNotiSetByMemberNo(memberNo);
        model.addAttribute("notiSets", notiSets);
        return "noti/notiSet";
    }

    /**
     * Noti set.
     *
     * @param c           the c
     * @param notiSetCode the noti set code
     * @param isReceived  the is received
     * @param model       the model
     * @since 2024 -08-14
     */
    @PostMapping("/notiSet")
    public void notiSet(@AuthenticationPrincipal CustomUserDetails c,
                        @RequestParam String notiSetCode,
                        @RequestParam String isReceived,
                        Model model) {

    }

    /**
     * 알림 모두 읽음 처리
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/markAllAsReadNoti")
    @ResponseBody
    public boolean markAllAsReadNoti(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        return notiService.markAllAsRead(receiverNo);
    }

    /**
     * 개별 알림 읽음 처리
     *
     * @param c      the c
     * @param notiNo the noti no
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/markAsRead")
    @ResponseBody
    public boolean markAsRead(@AuthenticationPrincipal CustomUserDetails c,
                                                           @RequestParam("notiNo") Integer notiNo) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        return notiService.markAsRead(receiverNo, notiNo);
    }

    /**
     * Delete all read noti response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/deleteAllReadNoti")
    @ResponseBody
    public boolean deleteAllReadNoti(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        return notiService.deleteAllReadNoti(receiverNo);
    }
}
