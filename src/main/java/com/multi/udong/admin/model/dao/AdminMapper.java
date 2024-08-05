package com.multi.udong.admin.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    List<MemberDTO> getAllMembers();
    List<MemberDTO> searchMember(@Param("memberId") String memberId, @Param("nickname") String nickname);

    // 신고 횟수 증가
    @Update("UPDATE MEMBER SET reported_cnt = reported_cnt + 1 WHERE member_no = #{memberNo}")
    void incrementReportedCnt(@Param("memberNo") int memberNo);

    // 신고 횟수 감소
    @Update("UPDATE MEMBER SET reported_cnt = reported_cnt - 1 WHERE member_no = #{memberNo} AND reported_cnt > 0")
    void decrementReportedCnt(@Param("memberNo") int memberNo);

    // 블랙리스트 처리
    @Update("UPDATE MEMBER SET is_blacked = 'Y', blacked_at = NOW() WHERE member_no = #{memberNo}")
    void blacklistMember(@Param("memberNo") int memberNo);

    // 블랙리스트 해제
    @Update("UPDATE MEMBER SET is_blacked = 'N', blacked_at = NULL WHERE member_no = #{memberNo}")
    void unblacklistMember(@Param("memberNo") int memberNo);

    // 특정 회원 조회
    @Select("SELECT * FROM MEMBER WHERE member_no = #{memberNo}")
    MemberDTO findMemberByNo(@Param("memberNo") int memberNo);

    //판매자관리
    List<MemBusDTO> selectSellers();
    void updateSellerStatus(@Param("memberNo") Integer memberNo, @Param("status") String status);

    @Select("SELECT approved_at FROM MEM_BUS WHERE member_no = #{memberNo}")
    String getApprovedAt(@Param("memberNo") int memberNo);

    @Select("SELECT mb.*, m.member_id as memberId, a.saved_name as attachmentName " +
            "FROM MEM_BUS mb " +
            "JOIN MEMBER m ON mb.member_no = m.member_no " +
            "LEFT JOIN ATTACHMENT a ON mb.member_no = a.target_no AND a.type_code = 'BUS' " +
            "ORDER BY mb.created_at DESC")
    List<Map<String, Object>> getAllSellers();

    @Update("UPDATE MEMBER SET authority = #{role} WHERE member_no = #{memberNo}")
    void updateMemberRole(@Param("memberNo") int memberNo, @Param("role") String role);
    MemBusDTO getSellerByMemberNo(Integer memberNo);
    AttachmentDTO getAttachmentByMemberNo(Long memberNo);
}
