package com.multi.udong.club.model.dao;

import com.multi.udong.club.model.dto.*;
import com.multi.udong.common.model.dto.AttachmentDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 모임 DAO
 *
 * @author 강성현
 * @since 2024 -07-25
 */
@Repository
public class ClubDAO {

    /**
     * 모임의 주제(카테고리) 리스트 조회
     *
     * @param sqlSession the sql session
     * @return the list
     * @since 2024 -07-25
     */
    public List<CategoryDTO> selectCategoryList(SqlSessionTemplate sqlSession) {

        return sqlSession.selectList("clubMapper.selectCategoryList");

    }


    /**
     * 모임 생성
     *
     * @param sqlSession the sql session
     * @param clubDTO    the club dto
     * @return the int
     * @since 2024 -07-25
     */
    public int insertClub(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.insert("clubMapper.insertClub", clubDTO);

    }


    /**
     * 모임 대표 이미지 업로드
     *
     * @param sqlSession    the sql session
     * @param attachmentDTO the attachment dto
     * @return the int
     * @since 2024 -07-25
     */
    public int insertClubImg(SqlSessionTemplate sqlSession, AttachmentDTO attachmentDTO) {

        return sqlSession.insert("clubMapper.insertClubImg", attachmentDTO);

    }


    /**
     * 모임 생성 직후 채팅방 코드 업데이트
     *
     * @param sqlSession the sql session
     * @param clubDTO    the club dto
     * @return the int
     * @since 2024 -07-25
     */
    public int updateChatroomCode(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.update("clubMapper.updateChatroomCode", clubDTO);

    }


    /**
     * 모임 생성 직후 모임장을 모임의 멤버로 insert
     *
     * @param sqlSession the sql session
     * @param clubDTO    the club dto
     * @return the int
     * @since 2024 -07-25
     */
    public int insertMaster(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.insert("clubMapper.insertMaster", clubDTO);

    }


    /**
     * 모임 리스트 조회
     *
     * @param sqlSession the sql session
     * @param filterDTO  the filter dto
     * @return the list
     * @since 2024 -07-25
     */
    public List<ClubDTO> selectClubList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("clubMapper.selectClubList", filterDTO);

    }


    /**
     * 모임의 개수 조회
     *
     * @param sqlSession the sql session
     * @param filterDTO  the filter dto
     * @return the int
     * @since 2024 -07-25
     */
    public int selectClubCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectClubCount", filterDTO);

    }


    /**
     * 모임 홈 조회
     *
     * @param sqlSession the sql session
     * @param requestDTO the request dto
     * @return the club dto
     * @since 2024 -07-25
     */
    public ClubDTO selectClubHome(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectOne("clubMapper.selectClubHome", requestDTO);

    }


    /**
     * 모임 가입 신청
     *
     * @param sqlSession the sql session
     * @param requestDTO the request dto
     * @return the int
     * @since 2024 -07-27
     */
    public int requestJoinClub(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.insert("clubMapper.requestJoinClub", requestDTO);

    }


    /**
     * 모임 가입 상태 확인
     *
     * @param sqlSession the sql session
     * @param requestDTO the request dto
     * @return the string
     * @since 2024 -07-27
     */
    public String checkJoinStatus(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectOne("clubMapper.checkJoinStatus", requestDTO);

    }


    /**
     * 모임 인원 현황 확인
     *
     * @param sqlSession the sql session
     * @param clubNo     the club no
     * @return the club dto
     * @since 2024 -07-27
     */
    public ClubDTO checkPersonnel(SqlSessionTemplate sqlSession, int clubNo) {

        return sqlSession.selectOne("clubMapper.checkPersonnel", clubNo);

    }


    /**
     * 모임장 확인
     *
     * @param sqlSession the sql session
     * @param clubNo     the club no
     * @return the int
     * @since 2024 -07-28
     */
    public int checkClubMaster(SqlSessionTemplate sqlSession, int clubNo) {

        return sqlSession.selectOne("clubMapper.checkClubMaster", clubNo);

    }


    /**
     * 모임 가입 신청 취소
     *
     * @param sqlSession the sql session
     * @param requestDTO the request dto
     * @return the int
     * @since 2024 -07-28
     */
    public int cancelJoinRequest(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.delete("clubMapper.cancelJoinRequest", requestDTO);

    }

    /**
     * 모임 탈퇴
     *
     * @param sqlSession the sql session
     * @param requestDTO the request dto
     * @return the int
     * @since 2024 -07-28
     */
    public int leaveClub(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.delete("clubMapper.leaveClub", requestDTO);

    }


    /**
     * 모임 해체
     *
     * @param sqlSession the sql session
     * @param requestDTO the request dto
     * @return the int
     * @since 2024 -07-28
     */
    public int deleteClub(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.update("clubMapper.deleteClub", requestDTO);

    }


    /**
     * 모임 신고
     *
     * @param sqlSession the sql session
     * @param reportDTO  the report dto
     * @return the int
     * @since 2024 -07-31
     */
    public int reportClub(SqlSessionTemplate sqlSession, ReportDTO reportDTO) {

        return sqlSession.insert("clubMapper.report", reportDTO);

    }


    /**
     * 모임 수정
     *
     * @param sqlSession the sql session
     * @param clubDTO    the club dto
     * @return the int
     * @since 2024 -08-02
     */
    public int updateClub(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.update("clubMapper.updateClub", clubDTO);

    }


    /**
     * 모임 이미지 수정
     *
     * @param sqlSession    the sql session
     * @param attachmentDTO the attachment dto
     * @return the int
     * @since 2024 -08-02
     */
    public int updateClubImg(SqlSessionTemplate sqlSession, AttachmentDTO attachmentDTO) {

        return sqlSession.update("clubMapper.updateClubImg", attachmentDTO);

    }


    /**
     * 모임 이미지 조회
     *
     * @param sqlSession the sql session
     * @param clubNo     the club no
     * @return the list
     * @since 2024 -08-02
     */
    public List<AttachmentDTO> selectClubImg(SqlSessionTemplate sqlSession, int clubNo) {

        return sqlSession.selectList("clubMapper.selectClubImg", clubNo);

    }


    /**
     * 기록 작성
     *
     * @param sqlSession the sql session
     * @param logDTO     the log dto
     * @return the int
     * @since 2024 -08-02
     */
    public int insertLog(SqlSessionTemplate sqlSession, LogDTO logDTO) {

        return sqlSession.insert("clubMapper.insertLog", logDTO);

    }


    /**
     * 기록 이미지 업로드
     *
     * @param sqlSession the sql session
     * @param attachment the attachment
     * @return the int
     * @since 2024 -08-02
     */
    public int insertLogImg(SqlSessionTemplate sqlSession, AttachmentDTO attachment) {

        return sqlSession.insert("clubMapper.insertLogImg", attachment);

    }

    public List<LogDTO> selectLogList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("clubMapper.selectLogList", filterDTO);

    }

    public int selectLogCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectLogCount", filterDTO);

    }

    public LogDTO selectLogDetail(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectOne("clubMapper.selectLogDetail", requestDTO);

    }

    public int addLogViews(SqlSessionTemplate sqlSession, int logNo) {

        return sqlSession.update("clubMapper.addLogViews", logNo);

    }

    public int insertReply(SqlSessionTemplate sqlSession, ReplyDTO replyDTO) {

        return sqlSession.insert("clubMapper.insertReply", replyDTO);

    }

    public int checkReplyWriter(SqlSessionTemplate sqlSession, int replyNo) {

        return sqlSession.selectOne("clubMapper.checkReplyWriter", replyNo);

    }

    public int updateReply(SqlSessionTemplate sqlSession, ReplyDTO replyDTO) {

        return sqlSession.update("clubMapper.updateReply", replyDTO);

    }

    public int deleteReply(SqlSessionTemplate sqlSession, ReplyDTO replyDTO) {

        return sqlSession.delete("clubMapper.deleteReply", replyDTO);

    }

    public int checkLogWriter(SqlSessionTemplate sqlSession, int logNo) {

        return sqlSession.selectOne("clubMapper.checkLogWriter", logNo);

    }

    public int reportLog(SqlSessionTemplate sqlSession, ReportDTO reportDTO) {

        return sqlSession.insert("clubMapper.report", reportDTO);

    }

    public AttachmentDTO selectAttachment(SqlSessionTemplate sqlSession, int fileNo) {

        return sqlSession.selectOne("clubMapper.selectAttachment", fileNo);

    }

    public int updateLog(SqlSessionTemplate sqlSession, LogDTO logDTO) {

        return sqlSession.update("clubMapper.updateLog", logDTO);

    }

    public int updateAttachment(SqlSessionTemplate sqlSession, AttachmentDTO newImg) {

        return sqlSession.update("clubMapper.updateAttachment", newImg);

    }

    public int deleteAttachment(SqlSessionTemplate sqlSession, AttachmentDTO deletedImg) {

        return sqlSession.delete("clubMapper.deleteAttachment", deletedImg);

    }

    public int insertAttachment(SqlSessionTemplate sqlSession, AttachmentDTO newImg) {

        return sqlSession.insert("clubMapper.insertAttachment", newImg);

    }

    public List<AttachmentDTO> selectLogImg(SqlSessionTemplate sqlSession, int logNo) {

        return sqlSession.selectList("clubMapper.selectLogImg", logNo);

    }

    public int deleteLog(SqlSessionTemplate sqlSession, LogDTO logDTO) {

        return sqlSession.update("clubMapper.deleteLog", logDTO);

    }


    public int insertLogLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.insert("clubMapper.insertLogLike", likeDTO);

    }

    public int deleteLogLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.delete("clubMapper.deleteLogLike", likeDTO);

    }

    public int insertReplyLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.insert("clubMapper.insertReplyLike", likeDTO);

    }

    public int deleteReplyLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.delete("clubMapper.deleteReplyLike", likeDTO);

    }


    public String checkIsClubDeleted(SqlSessionTemplate sqlSession, int clubNo) {

        return sqlSession.selectOne("clubMapper.checkIsClubDeleted", clubNo);

    }

    public String checkIsLogDeleted(SqlSessionTemplate sqlSession, int logNo) {

        return sqlSession.selectOne("clubMapper.checkIsLogDeleted", logNo);

    }

    public int deleteClubMember(SqlSessionTemplate sqlSession, ClubMemberDTO clubMemberDTO) {

        return sqlSession.delete("clubMapper.deleteClubMember", clubMemberDTO);

    }

    public List<ScheduleDTO> selectScheduleList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("clubMapper.selectScheduleList", filterDTO);

    }

    public int selectScheduleCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectScheduleCount", filterDTO);

    }

    public int insertSchedule(SqlSessionTemplate sqlSession, ScheduleDTO scheduleDTO) {

        return sqlSession.insert("clubMapper.insertSchedule", scheduleDTO);

    }

    public int insertScheduleParticipant(SqlSessionTemplate sqlSession, ScheduleDTO scheduleDTO) {

        return sqlSession.insert("clubMapper.insertScheduleParticipant", scheduleDTO);

    }

    public String checkIsScheduleDeleted(SqlSessionTemplate sqlSession, int scheduleNo) {

        return sqlSession.selectOne("clubMapper.checkIsScheduleDeleted", scheduleNo);

    }

    public ScheduleDTO selectScheduleDetail(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectOne("clubMapper.selectScheduleDetail", requestDTO);

    }

    public List<ClubMemberDTO> selectScheduleParticipants(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectList("clubMapper.selectScheduleParticipants", requestDTO);

    }

    public ScheduleDTO checkSchedulePersonnel(SqlSessionTemplate sqlSession, int scheduleNo) {

        return sqlSession.selectOne("clubMapper.checkSchedulePersonnel", scheduleNo);

    }

    public int joinSchedule(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.insert("clubMapper.joinSchedule", requestDTO);

    }

    public int checkScheduleJoinStatus(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectOne("clubMapper.checkScheduleJoinStatus", requestDTO);

    }

    public int cancelJoinSchedule(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.delete("clubMapper.cancelJoinSchedule", requestDTO);

    }

    public int checkScheduleMaker(SqlSessionTemplate sqlSession, int scheduleNo) {

        return sqlSession.selectOne("clubMapper.checkScheduleMaker", scheduleNo);

    }

    public int deleteSchedule(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.update("clubMapper.deleteSchedule", requestDTO);

    }

    public int deleteScheduleParticipants(SqlSessionTemplate sqlSession, int scheduleNo) {

        return sqlSession.delete("clubMapper.deleteScheduleParticipants", scheduleNo);

    }

    public List<AttachmentDTO> selectAlbumList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("clubMapper.selectAlbumList", filterDTO);

    }

    public int selectAlbumCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectAlbumCount", filterDTO);

    }

    public List<ClubMemberDTO> selectClubJoinRequestList(SqlSessionTemplate sqlSession, int clubNo) {

        return sqlSession.selectList("clubMapper.selectClubJoinRequestList", clubNo);

    }

    public int approveJoinRequest(SqlSessionTemplate sqlSession, ClubMemberDTO clubMemberDTO) {

        return sqlSession.update("clubMapper.approveJoinRequest", clubMemberDTO);

    }

    public int rejectJoinRequest(SqlSessionTemplate sqlSession, ClubMemberDTO clubMemberDTO) {

        return sqlSession.delete("clubMapper.rejectJoinRequest", clubMemberDTO);

    }

    public ClubDTO selectClubMemberList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectClubMemberList", filterDTO);

    }

    public int selectClubMemberCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectClubMemberCount", filterDTO);

    }

    public int delegateMaster(SqlSessionTemplate sqlSession, ClubMemberDTO clubMemberDTO) {

        return sqlSession.update("clubMapper.delegateMaster", clubMemberDTO);

    }

    public int kickMember(SqlSessionTemplate sqlSession, ClubMemberDTO clubMemberDTO) {

        return sqlSession.delete("clubMapper.kickMember", clubMemberDTO);

    }

    public String selectMemberProfileImg(SqlSessionTemplate sqlSession, int memberNo) {

        return sqlSession.selectOne("clubMapper.selectMemberProfileImg", memberNo);

    }

    public int saveChat(SqlSessionTemplate sqlSession, OutputChatMessage outputChatMessage) {

        return sqlSession.insert("clubMapper.saveChat", outputChatMessage);

    }

    public List<OutputChatMessage> selectInitialChatMessage(SqlSessionTemplate sqlSession, int clubNo) {

        return sqlSession.selectList("clubMapper.selectInitialChatMessage", clubNo);

    }

    public List<OutputChatMessage> selectOldChatMessage(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectList("clubMapper.selectOldChatMessage", requestDTO);

    }
}
