package com.multi.udong.login.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 재식
 * @since : 24. 7. 22.
 */
@Service
public class AuthenticationService {

    public Map<String, List<String>> getPermitListMap() {
        Map<String, List<String>> permitListMap = new HashMap<>();

        permitListMap.put("adminPermitList", Arrays.asList("/admin/**"));
        permitListMap.put("permitAllList", Arrays.asList("/", "/login/**", "/logout", "/signup/**", "/kakao/**", "/selectRole", "/isIdDuplicate", "/cs/**", "/chatbot/**"));

        return permitListMap;
    }
}