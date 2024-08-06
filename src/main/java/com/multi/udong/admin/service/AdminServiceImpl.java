package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dao.AdminMapper;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
            throw new RuntimeException("판매자 상태 업데이트 중 오류 발생", e); // 트랜잭션 롤백 유도
        }
    }

    @Override
    public MemBusDTO getSellerByMemberNo(Integer memberNo) {
        return adminMapper.getSellerByMemberNo(memberNo);
    }

    @Override
    public AttachmentDTO getAttachmentByMemberNo(Long memberNo) {
        return adminMapper.getAttachmentByMemberNo(memberNo);
    }

    @Override
    public void removeMemberFromBlacklist(Integer memberNo) {
        adminMapper.unblacklistMember(memberNo);
        adminMapper.resetReportedCount(memberNo); // 신고 카운트 초기화
    }

    @Override
    public void addMemberToBlacklist(Integer memberNo) {
        adminMapper.blacklistMember(memberNo);
    }
    @Override
    public List<MemberDTO> getBlacklistedMembers() {
        return adminMapper.findBlacklistedMembers();
    }
}
