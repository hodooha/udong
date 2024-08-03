package com.multi.udong.admin.model.dao;

import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.javassist.compiler.ast.Member;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<MemberDTO> getAllMembers();
    List<Member> searchMember(@Param("memberId") String memberId, @Param("nickname") String nickname);

}