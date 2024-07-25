package com.multi.udong.login.controller;

import com.multi.udong.member.model.dao.MemberDAO;
import com.multi.udong.login.model.dto.GoogleTokenResponse;
import com.multi.udong.login.model.dto.GoogleUserInfo;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.login.service.GoogleAuthClient;
import com.multi.udong.login.service.GoogleUserInfoClient;
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
    private final MemberDAO memberDAO;
    private final MemberServiceImpl memberService;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    /**
     * Google callback string.
     *
     * @param code               the code
     * @param httpServletRequest the http servlet request
     * @param model              the model
     * @return the string
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    @GetMapping("/google")
    public String googleLogin(@RequestParam("code") String code, HttpServletRequest httpServletRequest, Model model) throws Exception {
        GoogleTokenResponse googleTokenResponse = getAccessToken(code);
        GoogleUserInfo googleUserInfo = getUserInfo(googleTokenResponse.getAccess_token());

        if (googleUserInfo != null) {

            String googleMemberId = googleUserInfo.getId() + "g";
            String googleMemberPw = "googlePw1234";
            String googleEmail = googleUserInfo.getEmail();
            String googleNickname = googleUserInfo.getName();

            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMemberId(googleMemberId);
            memberDTO.setMemberPw(googleMemberPw);
            memberDTO.setEmail(googleEmail);
            memberDTO.setNickname(googleNickname);

            if (memberDAO.findMemberById(googleMemberId) == null) {
                memberService.signup(memberDTO);
            }

            model.addAttribute("member", memberDTO);
        }
        return "member/googleClose";
    }

    /**
     * Get access token google token response.
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
     * Get user info google user info.
     *
     * @param accessToken the access token
     * @return the google user info
     * @since 2024 -07-24
     */
    public GoogleUserInfo getUserInfo(String accessToken) {
        return googleUserInfoClient.getUserInfo("Bearer " + accessToken);
    }
}
