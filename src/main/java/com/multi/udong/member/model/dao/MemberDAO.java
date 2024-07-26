package com.multi.udong.member.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : 재식
 * @since : 24. 7. 23.
 */
@Mapper
public interface MemberDAO{

    int signup(MemberDTO memberDTO);

    int insertProfileImg(AttachmentDTO attachmentDTO);

    MemberDTO findMemberById(String memberId);

    MemberDTO findMemberByNickname(String nickname);

    int insertBusReg(MemBusDTO memBusDTO);

    int selectLastInsertId();

    int insertAddress(MemAddressDTO memAddressDTO);

    Long findCodeByAddress(MemAddressDTO memAddressDTO);

    int updateAddress(MemAddressDTO memAddressDTO);

    int insertAttachment(AttachmentDTO attachmentDTO);

    void updateProfileImg(AttachmentDTO attachmentDTO);

    void updateNickname(MemberDTO memberDTO);

    void updateEmail(MemberDTO memberDTO);

    void updatePhone(MemberDTO memberDTO);

    void updateMemberPw(MemberDTO memberDTO);
}
