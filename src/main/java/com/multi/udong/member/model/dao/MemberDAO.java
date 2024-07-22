package com.multi.udong.member.model.dao;

import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDAO{

    int signup(MemberDTO m);
    MemberDTO findMemberById(String memberId);
}
