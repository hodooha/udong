package com.multi.udong.member.service;

import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;

/**
 * @author : 재식
 * @since : 24. 7. 21.
 */
public interface MemberService {

    void signup(MemberDTO m) throws Exception;

    boolean isIdDuplicate(String memberId);

    void signupSeller(MemberDTO m, MemBusDTO memBusDTO) throws Exception;
}
