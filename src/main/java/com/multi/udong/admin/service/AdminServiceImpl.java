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
        List<MemBusDTO> sellers = adminMapper.selectSellers();
        for (MemBusDTO seller : sellers) {
            System.out.println("Seller ID: " + seller.getMemberNo());
            if (seller.getAttachmentDTO() != null) {
                System.out.println("  Attachment: " + seller.getAttachmentDTO().getSavedName());
            } else {
                System.out.println("  No attachment");
            }
        }
        return sellers;
    }
    @Override
    public AttachmentDTO getSellerAttachment(int memberNo) {
        return adminMapper.getAttachmentByMemberNo((long) memberNo);
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
        MemBusDTO seller = adminMapper.getSellerByMemberNo(memberNo);

        // 로그 출력
        if (seller != null) {
            System.out.println("approveStatus: " + seller.getApproveStatus());
        } else {
            System.out.println("Seller not found for memberNo: " + memberNo);
        }

        return seller;
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

    @Override
    public List<MemberDTO> getAllBlacklistRelatedMembers() {
        List<MemberDTO> blacklisted = adminMapper.findBlacklistedMembers();
        List<MemberDTO> unblacklisted = adminMapper.findUnblacklistedMembers();

        // 두 리스트를 결합하여 반환
        blacklisted.addAll(unblacklisted);
        return blacklisted;
    }
    @Override
    public List<MemberDTO> searchBlacklistedMembersByIdOrName(String search) {
        return adminMapper.searchBlacklistedMembersByIdOrName(search);
    }
}
