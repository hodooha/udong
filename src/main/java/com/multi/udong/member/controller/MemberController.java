package com.multi.udong.member.controller;

import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.member.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @RequestMapping("/myPage")
    public String myPage(){
        return "member/myPage";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model){
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "member/login";
    }

    @GetMapping("/signup")
    public String signup(Model model){
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
}
