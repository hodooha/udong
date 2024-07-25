package com.multi.udong.member.controller;

import com.multi.udong.login.controller.KakaoLoginController;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The type Member controller.
 *
 * @author 김재식
 * @since 2024 -07-24
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final KakaoLoginController kakaoLoginController;

    /**
     * Dash board.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/dashBoard")
    public void dashBoard(){}

    /**
     * Mem info.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/memInfo")
    public void memInfo(){}

    /**
     * Address.
     *
     * @param model the model
     * @since 2024 -07-24
     */
    @GetMapping("/address")
    public void address(Model model) {
        model.addAttribute("kakaoApiKey", kakaoLoginController.getKakaoApiKey());
    }

    /**
     * Act.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/act")
    public void act(){}

    /**
     * Message.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/message")
    public void message(){}

    /**
     * Noti set.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/notiSet")
    public void notiSet(){}

    /**
     * Mem del.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/memDel")
    public void memDel(){}

    /**
     * Insert address string.
     *
     * @param c             the c
     * @param memAddressDTO the mem address dto
     * @return the string
     * @since 2024 -07-25
     */
    @PostMapping("/insertAddress")
    public String insertAddress(@AuthenticationPrincipal CustomUserDetails c, MemAddressDTO memAddressDTO) {
        memAddressDTO.setMemberNo(c.getMemberDTO().getMemberNo());
        try {
            memberService.insertAddress(memAddressDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        memberService.updateMemberSession(c.getMemberDTO().getMemberId());
        return "member/address";
    }
}
