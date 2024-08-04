package com.multi.udong.admin.model.dao;

import com.multi.udong.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.javassist.compiler.ast.Member;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<MemberDTO> getAllMembers();
    List<Member> searchMember(@Param("memberId") String memberId, @Param("nickname") String nickname);

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
}
