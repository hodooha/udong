package com.multi.udong.member.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemPageDTO;
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

    void updateMember(AttachmentDTO attachmentDTO);

    void updateNickname(MemberDTO memberDTO);

    void updateEmail(MemberDTO memberDTO);

    void updatePhone(MemberDTO memberDTO);

    void updateMemberPw(MemberDTO memberDTO);

    List<LinkedHashMap<String, Object>> selectNewsBoard(MemPageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectNewsLike(MemPageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectNewsReply(MemPageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectClub(MemPageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectClubLog(MemPageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectClubSchedule(MemPageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectShareLike(MemPageDTO memberNo);

    List<LinkedHashMap<String, Object>> selectSaleBoard(MemPageDTO memberNo);

    List<Map<String, Object>> getNewsData(int memberNo);

    List<Map<String, Object>> getLendData(int memberNo);

    List<Map<String, Object>> getRentData(int memberNo);

    List<Map<String, Object>> getGiveData(int memberNo);

    List<Map<String, Object>> getClubData(int memberNo);

    List<Map<String, Object>> getScheduleData(int memberNo);

    int deleteMember(int memberNo);

    Map<String, Object> checkMember(int memberNo);
}
