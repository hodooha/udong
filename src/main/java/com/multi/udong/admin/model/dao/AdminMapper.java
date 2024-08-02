package com.multi.udong.admin.model.dao;

import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<MemberDTO> getAllMembers();
}