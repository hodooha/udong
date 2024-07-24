package com.multi.udong.member.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "NTSAPI", url = "http://api.odcloud.kr/api/")
public interface NTSAPI {

    @PostMapping("/nts-businessman/v1/validate")
    Map<String, Object> validateBusinessRegistration(
        @RequestParam("serviceKey") String serviceKey,
        @RequestBody Map<String, Object> requestBody

    );
}
