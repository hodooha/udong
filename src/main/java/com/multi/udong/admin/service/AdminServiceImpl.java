package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dao.AdminMapper;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        return adminMapper.getAllMembers();
    }

    @Override
    public List<MemberDTO> searchMembersByIdOrNickname(String search) {
        return adminMapper.searchMember(search, search);
    }

    @Override
    public List<MemBusDTO> getAllSellers() {
        return adminMapper.selectSellers();
    }

    @Override
    public boolean updateSellerStatus(Integer memberNo, String status) {
        try {
            adminMapper.updateSellerStatus(memberNo, status);
            if ("Y".equals(status)) {
                adminMapper.updateMemberRole(memberNo, "ROLE_SELLER");
            } else if ("N".equals(status)) {
                adminMapper.updateMemberRole(memberNo, "ROLE_MEMBER");  // 취소 시 일반 회원으로 변경
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MemBusDTO getSellerByMemberNo(Integer memberNo) {
        return adminMapper.getSellerByMemberNo(memberNo);
    }

    @Override
    public AttachmentDTO getAttachmentByMemberNo(Long memberNo) {
        // AdminMapper를 통해 memberNo에 해당하는 첨부 파일 정보를 가져옴
        return adminMapper.getAttachmentByMemberNo(memberNo);
    }
}
