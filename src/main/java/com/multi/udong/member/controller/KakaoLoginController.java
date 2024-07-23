package com.multi.udong.member.controller;

import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberServiceImpl;
import com.multi.udong.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@Controller
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoLoginController {

    private final CustomUserDetailsService customUserDetailsService;
	private final MemberServiceImpl memberService;

	@Value("${kakao.api.key}")
	private String KakaoApiKey;

	// API키 가져오기
	@GetMapping("/getKakaoApiKey")
	@ResponseBody
    public String getKakaoApiKey() {
        return KakaoApiKey;
    }

	// 카카오Id 중복조회
	@PostMapping("/idDuplicateCheck")
    @ResponseBody
    public ResponseEntity<?> idDuplicateCheck(@RequestParam("memberId") String memberId) {
        try {
            customUserDetailsService.loadUserByUsername(memberId);
            return ResponseEntity.ok(Collections.singletonMap("idExists", true));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.ok(Collections.singletonMap("idExists", false));
        }
    }

	// 카카오 회원 가입
	@PostMapping("/kakaoSignUp")
    @ResponseBody
    public ResponseEntity<?> kakaoSignUp(MemberDTO m) {
		String kakaoPw = "kakaoPw1234";
		m.setMemberPw(kakaoPw);

		try {
			memberService.signup(m);
		} catch (Exception e) {
			e.printStackTrace();
		}

        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }
}
