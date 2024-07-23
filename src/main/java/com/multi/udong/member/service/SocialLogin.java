package com.multi.udong.member.service;

import com.multi.udong.config.OpenFeignConfig;
import com.multi.udong.member.model.dto.MemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SocialLogin", url = "http://localhost:8090", configuration = OpenFeignConfig.class)
public interface SocialLogin {

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody MemberDTO m, @RequestHeader("X-CSRF-TOKEN") String csrfToken);
}
