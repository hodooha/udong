package com.multi.udong.login.controller;

import com.multi.udong.login.model.dto.GoogleTokenResponse;
import com.multi.udong.login.model.dto.GoogleUserInfo;
import com.multi.udong.login.service.GoogleAuthClient;
import com.multi.udong.login.service.GoogleUserInfoClient;
import com.multi.udong.member.model.dao.MemberMapper;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The type Google login controller.
 *
 * @author 김재식
 * @since 2024 -07-24
 */
@RequestMapping("/login/oauth2")
@Controller
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleAuthClient googleAuthClient;
    private final GoogleUserInfoClient googleUserInfoClient;
    private final MemberMapper memberMapper;
    private final MemberServiceImpl memberService;
    private final LoginController loginController;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    /**
     * 구글 사용자정보로 로그인함
     *
     * @param code    the code
     * @param request the request
     * @param model   the model
     * @return the string
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    @GetMapping("/google")
    public String googleLogin(@RequestParam("code") String code,
                              HttpServletRequest request,
                              Model model) throws Exception {
        
        // code로 엑세스토큰을 받아옴
        GoogleTokenResponse googleTokenResponse = getAccessToken(code);
        
        // 엑세스토큰으로 구글 사용자정보를 받아옴
        GoogleUserInfo googleUserInfo = getUserInfo(googleTokenResponse.getAccess_token());

        // 제대로 사용자정보를 받아왔다면
        if (googleUserInfo != null) {

            // 사용자정보 전처리
            String googleMemberId = googleUserInfo.getId() + "g";
            String googleMemberPw = "googlePw1234";
            String googleEmail = googleUserInfo.getEmail();
            String googleNickname = googleUserInfo.getName();

            // 전처리 정보를 memberDTO에 저장
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMemberId(googleMemberId);
            memberDTO.setMemberPw(googleMemberPw);
            memberDTO.setEmail(googleEmail);
            memberDTO.setNickname(googleNickname);

            // 가입되어있지 않다면 회원가입 진행
            if (memberMapper.findMemberById(googleMemberId) == null) {
                memberService.signup(memberDTO);
            }

            // 로그인 작업 수행
            loginController.authenticateUserAndSetSession(memberDTO, request);
        }

        return "member/googleClose";
    }

    /**
     * 구글 리디렉션 URL에서 받은 code로 엑세스토큰을 받아옴
     *
     * @param code the code
     * @return the google token response
     * @since 2024 -07-24
     */
    public GoogleTokenResponse getAccessToken(String code) {
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        return googleAuthClient.getAccessToken(body);
    }

    /**
     * 엑세스토큰으로 사용자정보를 받아옴
     *
     * @param accessToken the access token
     * @return the google user info
     * @since 2024 -07-24
     */
    public GoogleUserInfo getUserInfo(String accessToken) {
        return googleUserInfoClient.getUserInfo("Bearer " + accessToken);
    }
}
