package com.multi.udong.member.controller;

import com.multi.udong.member.model.dao.MemberDAO;
import com.multi.udong.member.model.dto.GoogleTokenResponse;
import com.multi.udong.member.model.dto.GoogleUserInfo;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.GoogleAuthClient;
import com.multi.udong.member.service.GoogleUserInfoClient;
import com.multi.udong.member.service.MemberServiceImpl;
import com.multi.udong.member.service.SocialLogin;
import com.multi.udong.security.CsrfTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@RequestMapping("/login/oauth2")
@Controller
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleAuthClient googleAuthClient;
    private final GoogleUserInfoClient googleUserInfoClient;
    private final MemberDAO memberDAO;
    private final MemberServiceImpl memberService;
    private final SocialLogin socialLogin;
    private final CsrfTokenProvider csrfTokenProvider;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    /**
     * Google callback string.
     *
     * @param code the code
     * @return the string
     * @since 2024 -07-23
     */
    @GetMapping("/google")
    public String googleLogin(@RequestParam("code") String code, HttpServletRequest httpServletRequest) throws Exception {
        GoogleTokenResponse googleTokenResponse = getAccessToken(code);
        GoogleUserInfo googleUserInfo = getUserInfo(googleTokenResponse.getAccess_token());

        if (googleUserInfo != null) {

            String googleMemberId = googleUserInfo.getEmail() + "g";
            String googleMemberPw = "googlePw1234";
            String googleEmail = googleUserInfo.getEmail();

            Random random = new Random();
            int randomNumber = random.nextInt(100000);
            String googleNickname = String.format("google-%05d", randomNumber);

            MemberDTO m = new MemberDTO();
            m.setMemberId(googleMemberId);
            m.setMemberPw(googleMemberPw);
            m.setEmail(googleEmail);
            m.setNickname(googleNickname);

            if (memberDAO.findMemberById(googleMemberId) == null) {
                memberService.signup(m);
            }

            System.out.println("멤버DTO" + m);

            ResponseEntity<String> response = socialLogin.login(m, csrfTokenProvider.getToken());

            System.out.println("response :" + response);
        }
        return "member/googleClose";
    }

    public GoogleTokenResponse getAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        return googleAuthClient.getAccessToken(body);
    }

    public GoogleUserInfo getUserInfo(String accessToken) {
        return googleUserInfoClient.getUserInfo("Bearer " + accessToken);
    }
}
