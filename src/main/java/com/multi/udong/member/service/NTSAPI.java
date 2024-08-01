package com.multi.udong.member.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * The interface Ntsapi.
 *
 * @author 김재식
 * @since 2024 -08-01
 */
@FeignClient(name = "NTSAPI", url = "http://api.odcloud.kr/api/")
public interface NTSAPI {

    /**
     * Validate business registration map.
     *
     * @param serviceKey  the service key
     * @param requestBody the request body
     * @return the map
     * @since 2024 -08-01
     */
    @PostMapping("/nts-businessman/v1/validate")
    Map<String, Object> validateBusinessRegistration(
        @RequestParam("serviceKey") String serviceKey,
        @RequestBody Map<String, Object> requestBody

    );
}
