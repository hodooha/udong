package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dao.AdminMapper;
import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.javassist.compiler.ast.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminMapper adminMapper;

    @Autowired
    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public List<MemberDTO> getAllMembers() {
        return adminMapper.getAllMembers();
    }

    public List<Member> searchMembersByIdOrNickname(String search) {
        return adminMapper.searchMember(search, search);
    }
}