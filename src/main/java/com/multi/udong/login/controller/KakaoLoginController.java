package com.multi.udong.login.controller;

import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberServiceImpl;
import com.multi.udong.login.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


/**
 * The type Kakao login controller.
 *
 * @author 김재식
 * @since 2024 -07-24
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoLoginController {

    private final CustomUserDetailsService customUserDetailsService;
	private final MemberServiceImpl memberService;

	@Value("${kakao.api.key}")
	private String KakaoApiKey;

    @Value("${kakao.client.pw}")
    private String KakaoPW;

    /**
     * Get kakao api key string.
     *
     * @return the string
     * @since 2024 -07-24
     */
	@GetMapping("/getKakaoApiKey")
	@ResponseBody
    public String getKakaoApiKey() {
        return KakaoApiKey;
    }

    @GetMapping("/getKakaoPW")
    @ResponseBody
    public String getKakaoPW() {
        return KakaoPW;
    }

    /**
     * 카카오 사용자 정보로 가입되었는지 중복확인
     *
     * @param memberId the member id
     * @return the response entity
     * @since 2024 -07-24
     */
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

    /**
     * 카카오 사용자 정보로 회원가입
     *
     * @param memberDTO the member dto
     * @return the response entity
     * @since 2024 -07-24
     */
	@PostMapping("/kakaoSignUp")
    @ResponseBody
    public ResponseEntity<?> kakaoSignUp(MemberDTO memberDTO) {

		memberDTO.setMemberPw(KakaoPW);

		try {
			memberService.signup(memberDTO);

		} catch (Exception e) {
			e.printStackTrace();
		}

        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }
}
