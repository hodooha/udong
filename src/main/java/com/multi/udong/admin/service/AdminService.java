package com.multi.udong.admin.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;

import java.util.List;

public interface AdminService {
    List<MemberDTO> getAllMembers();
    List<MemberDTO> searchMembersByIdOrNickname(String search);
    List<MemBusDTO> getAllSellers();
    AttachmentDTO getSellerAttachment(int memberNo);
    boolean updateSellerStatus(Integer memberNo, String status);
    MemBusDTO getSellerByMemberNo(Integer memberNo);
    AttachmentDTO getAttachmentByMemberNo(Long memberNo);
    List<MemberDTO> getBlacklistedMembers();
    void removeMemberFromBlacklist(Integer memberNo);
    void addMemberToBlacklist(Integer memberNo);
    List<MemberDTO> getAllBlacklistRelatedMembers();

}