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

        return sqlSession.delete("clubMapper.deleteClub", requestDTO);

    }


    /**
     * 모임 이미지 삭제
     *
     * @param sqlSession the sql session
     * @param clubNo     the club no
     * @return the int
     * @since 2024 -08-02
     */
    public int deleteClubImg(SqlSessionTemplate sqlSession, int clubNo) {

        return sqlSession.delete("clubMapper.deleteClubImg", clubNo);

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

        return sqlSession.insert("clubMapper.reportClub", reportDTO);

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

    public String checkAdmin(SqlSessionTemplate sqlSession, int memberNo) {

        return sqlSession.selectOne("clubMapper.checkAdmin", memberNo);

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

    public int insertReply(SqlSessionTemplate sqlSession, ReplyDTO replyDTO) {

        return sqlSession.insert("clubMapper.insertReply", replyDTO);

    }
}
