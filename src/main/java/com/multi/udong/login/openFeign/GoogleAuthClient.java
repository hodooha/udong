package com.multi.udong.login.openFeign;

import com.multi.udong.login.model.dto.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "googleAuth", url = "https://oauth2.googleapis.com/")
public interface GoogleAuthClient {

    @PostMapping("/token")
    GoogleTokenResponse getAccessToken(@RequestBody MultiValueMap<String, String> body);
}

