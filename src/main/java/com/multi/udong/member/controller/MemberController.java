package com.multi.udong.member.controller;

import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.member.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @RequestMapping("/myPage")
    public String myPage() {
        return "member/myPage";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        System.out.println("회원가입 시작");
        model.addAttribute("memberDTO", new MemberDTO());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(MemberDTO m) {

        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        String randomNickname = String.format("member-%05d", randomNumber);
        m.setNickname(randomNickname);

        try {
            System.out.println("memberDTO :" + m);
            memberService.signup(m);
            System.out.println("회원가입 성공");
            return "redirect:/login";
        } catch (Exception e) {
            System.out.println("회원가입 실패");
            return "redirect:/signup";
        }
    }

    @PostMapping("/isIdDuplicate")
    public ResponseEntity<String> isIdDuplicate(@RequestBody MemberDTO request) {
        boolean isDuplicate = memberService.isIdDuplicate(request.getMemberId());
        if (isDuplicate) {
            return ResponseEntity.ok("duplicate");
        } else {
            return ResponseEntity.ok("available");
        }
    }

    @GetMapping("/login/oauth2/google")
    public String googleCallback(@RequestParam String code) {
        return "redirect:/login";
    }
}

