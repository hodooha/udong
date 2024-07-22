package com.multi.udong.member.service;

import com.multi.udong.member.model.dto.MemberDTO;

public interface MemberService {
    void signup(MemberDTO m) throws Exception;
}
