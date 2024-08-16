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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

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
     * 알림 가져오기
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @GetMapping("/getNoti")
    @ResponseBody
    public List<NotiDTO> getNoti(@AuthenticationPrincipal CustomUserDetails c) {

        if (c != null) {
            int memberNo = c.getMemberDTO().getMemberNo();
            return notiService.getNoti(memberNo);
        }

        return null;
    }

    /**
     * 안읽은 알림 개수 가져오기
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @GetMapping("/getUnreadNotiCount")
    @ResponseBody
    public int getUnreadNotiCount(@AuthenticationPrincipal CustomUserDetails c) {

        if (c != null) {
            int receiverNo = c.getMemberDTO().getMemberNo();
            return notiService.getUnreadNotiCount(receiverNo);
        }

        return 0;
    }

    /**
     * 알림 설정 페이지
     *
     * @param c     the c
     * @param model the model
     * @return the string
     * @since 2024 -08-14
     */
    @GetMapping("/notiSet")
    public String notiSet(@AuthenticationPrincipal CustomUserDetails c, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        List<NotiSetDTO> notiSets = notiService.getNotiSetByMemberNo(memberNo);
        model.addAttribute("notiSets", notiSets);
        return "notification/notiSet";
    }

    /**
     * 알림 설정 업데이트
     *
     * @param c                  the c
     * @param params             the params
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -08-15
     */
    @PostMapping("/notiSet")
    public String updateNotiSet(@AuthenticationPrincipal CustomUserDetails c,
                              @RequestParam Map<String, String> params,
                              RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();

        params.remove("_csrf");

        if (notiService.updateNotiSet(memberNo, params)) {
            redirectAttributes.addFlashAttribute("alert", "알림 설정이 변경되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } else {
            redirectAttributes.addFlashAttribute("alert", "알림 설정 변경에 실패했습니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
        }

        return "redirect:/noti/notiSet";
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
     * 읽은 알림 모두 삭제
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
