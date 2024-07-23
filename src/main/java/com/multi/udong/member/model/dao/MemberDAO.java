package com.multi.udong.member.model.dao;

import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : 재식
 * @since : 24. 7. 23.
 */
@Mapper
public interface MemberDAO{

    int signup(MemberDTO m);

    MemberDTO findMemberById(String memberId);

    int insertBusReg(MemBusDTO memBusDTO);
    int selectLastInsertId();
}
