package com.multi.udong.member.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 재식
 * @since : 24. 7. 23.
 */
@Mapper
public interface MemberMapper {

    int signup(MemberDTO memberDTO);

    int insertProfileImg();

    MemberDTO findMemberById(String memberId);

    MemberDTO findMemberByNickname(String nickname);

    int insertBusReg(MemBusDTO memBusDTO);

    int insertAddress(MemAddressDTO memAddressDTO);

    Long findCodeByAddress(MemAddressDTO memAddressDTO);

    int updateAddress(MemAddressDTO memAddressDTO);

    int insertAttachment(AttachmentDTO attachmentDTO);

    void updateProfileImg(AttachmentDTO attachmentDTO);

    void updateMember(AttachmentDTO attachmentDTO);

    void updateNickname(MemberDTO memberDTO);

    void updateEmail(MemberDTO memberDTO);

    void updatePhone(MemberDTO memberDTO);

    void updateMemberPw(MemberDTO memberDTO);

    List<LinkedHashMap<String, Object>> selectNewsBoard(PageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectNewsLike(PageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectNewsReply(PageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectClub(PageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectClubLog(PageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectClubSchedule(PageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectShareLike(PageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectSaleBoard(PageDTO memberNo);

    List<Map<String, Object>> getNewsData(int memberNo);

    List<Map<String, Object>> getLendData(int memberNo);

    List<Map<String, Object>> getRentData(int memberNo);

    List<Map<String, Object>> getGiveData(int memberNo);

    List<Map<String, Object>> getClubData(int memberNo);

    List<Map<String, Object>> getScheduleData(int memberNo);

    int deleteMember(int memberNo);

    Map<String, Object> checkMember(int memberNo);

    void updateLastLoginAt(String memberId);
}
