package com.multi.udong.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @since 2024 -07-24
     */
    @GetMapping("/address")
    public void address(){}

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

}
