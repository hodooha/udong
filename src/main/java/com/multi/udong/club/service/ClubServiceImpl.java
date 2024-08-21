package com.multi.udong.club.service;

import com.multi.udong.club.model.dao.ClubDAO;
import com.multi.udong.club.model.dto.*;
import com.multi.udong.common.model.dto.AttachmentDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 모임 service
 *
 * @author 강성현
 * @since 2024 -07-25
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class ClubServiceImpl implements ClubService {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private final ClubDAO clubDAO;

    /**
     * 모임 service 생성자
     *
     * @param clubDAO the club dao
     */
    public ClubServiceImpl(ClubDAO clubDAO) {
        this.clubDAO = clubDAO;
    }


    /**
     * 모임의 주제(카테고리) 리스트 조회
     *
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public List<CategoryDTO> selectCategoryList() throws Exception {

        return clubDAO.selectCategoryList(sqlSession);

    }


    /**
     * 모임 생성
     *
     * @param clubDTO the club dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public int insertClub(ClubDTO clubDTO) throws Exception {

        int result = 0;

        // 모임 데이터 insert
        int clubResult = clubDAO.insertClub(sqlSession, clubDTO);

        // insert한 모임no를 targetNo에 set한 후 모임 이미지 insert
        int clubNo = clubDTO.getClubNo();
        AttachmentDTO attachmentDTO = clubDTO.getAttachment().get(0);
        attachmentDTO.setTargetNo(clubNo);
        int attachmentResult = clubDAO.insertClubImg(sqlSession, attachmentDTO);

        if(clubResult <= 0 || attachmentResult <= 0) {

            throw new Exception("모임 생성에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

        } else {

            // clubNo를 붙인 채팅방 code를 만들어 다시 clubDTO에 set
            String chatroomCode = "chatroom" + clubNo;
            clubDTO.setChatroomCode(chatroomCode);

            // 비어있던 chatroom 컬럼에 code를 update
            int chatroomResult = clubDAO.updateChatroomCode(sqlSession, clubDTO);

            // 모임 멤버 테이블에 모임 생성자를 insert
            int masterResult = clubDAO.insertMaster(sqlSession, clubDTO);

            if(chatroomResult <= 0 || masterResult <= 0) {

                throw new Exception("모임 생성 후 챗룸 및 모임장 insert에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

            } else {

                result = 1;

            }

        }

        return result;

    }


    /**
     * 모임 리스트 조회
     *
     * @param filterDTO the filter dto
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public List<ClubDTO> selectClubList(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubList(sqlSession, filterDTO);

    }


    /**
     * 모임의 개수 조회
     *
     * @param filterDTO the filter dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public int selectClubCount(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubCount(sqlSession, filterDTO);

    }


    /**
     * 모임 홈 조회
     *
     * @param requestDTO the request dto
     * @return the club dto
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public ClubDTO selectClubHome(RequestDTO requestDTO) throws Exception {

        return clubDAO.selectClubHome(sqlSession, requestDTO);

    }


    /**
     * 모임 가입 신청
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-27
     */
    @Override
    public int requestJoinClub(RequestDTO requestDTO) throws Exception {

        return clubDAO.requestJoinClub(sqlSession, requestDTO);

    }


    /**
     * 모임 가입 상태 확인
     *
     * @param requestDTO the request dto
     * @return the string
     * @throws Exception the exception
     * @since 2024 -07-27
     */
    @Override
    public String checkJoinStatus(RequestDTO requestDTO) throws Exception {

        return clubDAO.checkJoinStatus(sqlSession, requestDTO);

    }


    /**
     * 모임 인원 현황 확인
     *
     * @param clubNo the club no
     * @return the club dto
     * @throws Exception the exception
     * @since 2024 -07-27
     */
    @Override
    public ClubDTO checkPersonnel(int clubNo) throws Exception {

        return clubDAO.checkPersonnel(sqlSession, clubNo);

    }


    /**
     * 모임장 확인
     *
     * @param clubNo the club no
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int checkClubMaster(int clubNo) throws Exception {

        return clubDAO.checkClubMaster(sqlSession, clubNo);

    }


    /**
     * 모임 가입 신청 취소
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int cancelJoinRequest(RequestDTO requestDTO) throws Exception {

        return clubDAO.cancelJoinRequest(sqlSession, requestDTO);

    }


    /**
     * 모임 탈퇴
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int leaveClub(RequestDTO requestDTO) throws Exception {

        return clubDAO.leaveClub(sqlSession, requestDTO);

    }


    /**
     * 모임 해체
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int deleteClub(RequestDTO requestDTO) throws Exception {

        int result = 0;

        int clubResult = clubDAO.deleteClub(sqlSession, requestDTO);

        if(clubResult == 1) {

            ClubMemberDTO clubMemberDTO = new ClubMemberDTO();
            clubMemberDTO.setClubNo(requestDTO.getClubNo());

            int clubMemberResult = clubDAO.deleteClubMember(sqlSession, clubMemberDTO);

            if(clubMemberResult > 0) {

                result = 1;

            }

        }

        return result;

    }


    /**
     * 모임 신고
     *
     * @param reportDTO the report dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    @Override
    public int reportClub(ReportDTO reportDTO) throws Exception {

        return clubDAO.reportClub(sqlSession, reportDTO);

    }


    /**
     * 모임 수정
     *
     * @param clubDTO the club dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -08-02
     */
    @Override
    public int updateClub(ClubDTO clubDTO) throws Exception {

        int result = 0;

        // 모임 데이터 update
        int clubResult = clubDAO.updateClub(sqlSession, clubDTO);

        if(clubResult == 1) {

            // 이미지 수정 없이 모임 데이터만 수정 시 result는 1
            result = 1;

            System.out.println("###### 새 이미지 업로드 여부: " + clubDTO.getAttachment());

            // 이미지를 새로 업로드 했으면 이미지 update
            if(clubDTO.getAttachment() != null) {

                int clubNo = clubDTO.getClubNo();
                AttachmentDTO attachmentDTO = clubDTO.getAttachment().get(0);
                attachmentDTO.setTargetNo(clubNo);

                int attachmentResult = clubDAO.updateClubImg(sqlSession, attachmentDTO);

                if(attachmentResult == 1) {

                    // 이미지도 수정 시 result는 2
                    result = 2;

                }
                else {

                    throw new Exception("모임 이미지 수정에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

                }

            }

        }
        else {

            throw new Exception("모임 수정에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

        }

        return result;

    }


    /**
     * 모임 이미지 조회
     *
     * @param clubNo the club no
     * @return the list
     * @throws Exception the exception
     * @since 2024 -08-02
     */
    @Override
    public List<AttachmentDTO> selectClubImg(int clubNo) throws Exception {

        return clubDAO.selectClubImg(sqlSession, clubNo);

    }


    /**
     * 기록 작성
     *
     * @param logDTO the log dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -08-02
     */
    @Override
    public int insertLog(LogDTO logDTO) throws Exception {

        int result = 0;

        int logResult = clubDAO.insertLog(sqlSession, logDTO);

        if(logResult == 1) {

            int attachmentResult = 0;

            List<AttachmentDTO> attachmentList = logDTO.getAttachments();

            if(attachmentList != null) {

                int logNo = logDTO.getLogNo();

                for(AttachmentDTO attachment : attachmentList) {

                    attachment.setTargetNo(logNo);
                    attachmentResult += clubDAO.insertLogImg(sqlSession, attachment);

                }

                if(attachmentResult == attachmentList.size()) {

                    result = 1;

                }
                else {

                    throw new Exception("기록 이미지 업로드에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

                }

            }

        }
        else {

            throw new Exception("기록 작성에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

        }

        return result;

    }

    @Override
    public List<LogDTO> selectLogList(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectLogList(sqlSession, filterDTO);

    }

    @Override
    public int selectLogCount(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectLogCount(sqlSession, filterDTO);

    }

    @Override
    public LogDTO selectLogDetail(RequestDTO requestDTO) throws Exception {

        return clubDAO.selectLogDetail(sqlSession, requestDTO);

    }

    @Override
    public int addLogViews(int logNo) throws Exception {

        return clubDAO.addLogViews(sqlSession, logNo);

    }

    @Override
    public String checkIsClubDeleted(int clubNo) throws Exception {

        return clubDAO.checkIsClubDeleted(sqlSession, clubNo);

    }

    @Override
    public String checkIsLogDeleted(int logNo) throws Exception {

        return clubDAO.checkIsLogDeleted(sqlSession, logNo);

    }

    @Override
    public List<ScheduleDTO> selectScheduleList(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectScheduleList(sqlSession, filterDTO);

    }

    @Override
    public int selectScheduleCount(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectScheduleCount(sqlSession, filterDTO);

    }

    @Override
    public int insertSchedule(ScheduleDTO scheduleDTO) throws Exception {

        int result = 0;

        int scheduleResult = clubDAO.insertSchedule(sqlSession, scheduleDTO);

        if(scheduleResult == 1) {

            int participantResult = clubDAO.insertScheduleParticipant(sqlSession, scheduleDTO);

            if(participantResult == 1) {

                result = 1;

            }

        }

        return result;

    }

    @Override
    public String checkIsScheduleDeleted(int scheduleNo) throws Exception {

        return clubDAO.checkIsScheduleDeleted(sqlSession, scheduleNo);

    }

    @Override
    public ScheduleDTO selectScheduleDetail(RequestDTO requestDTO) throws Exception {

        ScheduleDTO scheduleDetail = clubDAO.selectScheduleDetail(sqlSession, requestDTO);
        List<ClubMemberDTO> participants = clubDAO.selectScheduleParticipants(sqlSession, requestDTO);

        scheduleDetail.setParticipants(participants);

        return scheduleDetail;

    }

    @Override
    public ScheduleDTO checkSchedulePersonnel(int scheduleNo) throws Exception {

        return clubDAO.checkSchedulePersonnel(sqlSession, scheduleNo);

    }

    @Override
    public int joinSchedule(RequestDTO requestDTO) throws Exception {

        return clubDAO.joinSchedule(sqlSession, requestDTO);

    }

    @Override
    public int checkScheduleJoinStatus(RequestDTO requestDTO) throws Exception {

        return clubDAO.checkScheduleJoinStatus(sqlSession, requestDTO);

    }

    @Override
    public int cancelJoinSchedule(RequestDTO requestDTO) throws Exception {

        return clubDAO.cancelJoinSchedule(sqlSession, requestDTO);

    }

    @Override
    public int checkScheduleMaker(int scheduleNo) throws Exception {

        return clubDAO.checkScheduleMaker(sqlSession, scheduleNo);

    }

    @Override
    public int deleteSchedule(RequestDTO requestDTO) throws Exception {

        int result = 0;

        int scheduleResult = clubDAO.deleteSchedule(sqlSession, requestDTO);

        if(scheduleResult == 1) {

            int scheduleNo = requestDTO.getScheduleNo();
            int participantResult = clubDAO.deleteScheduleParticipants(sqlSession, scheduleNo);

            if(participantResult > 0) {

                result = 1;

            }

        }

        return result;

    }

    @Override
    public List<AttachmentDTO> selectAlbumList(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectAlbumList(sqlSession, filterDTO);

    }

    @Override
    public int selectAlbumCount(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectAlbumCount(sqlSession, filterDTO);

    }

    @Override
    public List<ClubMemberDTO> selectClubJoinRequestList(int clubNo) throws Exception {

        return clubDAO.selectClubJoinRequestList(sqlSession, clubNo);

    }

    @Override
    public int approveJoinRequest(ClubMemberDTO clubMemberDTO) throws Exception {

        return clubDAO.approveJoinRequest(sqlSession, clubMemberDTO);

    }

    @Override
    public int rejectJoinRequest(ClubMemberDTO clubMemberDTO) throws Exception {

        return clubDAO.rejectJoinRequest(sqlSession, clubMemberDTO);

    }

    @Override
    public ClubDTO selectClubMemberList(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubMemberList(sqlSession, filterDTO);

    }

    @Override
    public int selectClubMemberCount(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubMemberCount(sqlSession, filterDTO);

    }

    @Override
    public int delegateMaster(ClubMemberDTO clubMemberDTO) throws Exception {

        return clubDAO.delegateMaster(sqlSession, clubMemberDTO);

    }

    @Override
    public int kickMember(ClubMemberDTO clubMemberDTO) throws Exception {

        return clubDAO.kickMember(sqlSession, clubMemberDTO);

    }

    @Override
    public String selectMemberProfileImg(int memberNo) throws Exception {

        return clubDAO.selectMemberProfileImg(sqlSession, memberNo);

    }

    @Override
    public int saveChat(OutputChatMessage outputChatMessage) throws Exception {

        return clubDAO.saveChat(sqlSession, outputChatMessage);

    }

    @Override
    public List<OutputChatMessage> selectInitialChatMessage(int clubNo) throws Exception {

        return clubDAO.selectInitialChatMessage(sqlSession, clubNo);

    }

    @Override
    public List<OutputChatMessage> selectOldChatMessage(RequestDTO requestDTO) throws Exception {

        return clubDAO.selectOldChatMessage(sqlSession, requestDTO);

    }

    @Override
    public int insertReply(ReplyDTO replyDTO) throws Exception {

        return clubDAO.insertReply(sqlSession, replyDTO);

    }

    @Override
    public int checkReplyWriter(int replyNo) throws Exception {

        return clubDAO.checkReplyWriter(sqlSession, replyNo);

    }

    @Override
    public int updateReply(ReplyDTO replyDTO) throws Exception {

        return clubDAO.updateReply(sqlSession, replyDTO);

    }

    @Override
    public int deleteReply(ReplyDTO replyDTO) throws Exception {

        return clubDAO.deleteReply(sqlSession, replyDTO);

    }

    @Override
    public int checkLogWriter(int logNo) throws Exception {

        return clubDAO.checkLogWriter(sqlSession, logNo);

    }

    @Override
    public int reportLog(ReportDTO reportDTO) throws Exception {

        return clubDAO.reportLog(sqlSession, reportDTO);

    }

    @Override
    public int updateLog(LogDTO logDTO) throws Exception {

        return clubDAO.updateLog(sqlSession, logDTO);

    }

    @Override
    public AttachmentDTO selectAttachment(int fileNo) throws Exception {

        return clubDAO.selectAttachment(sqlSession, fileNo);

    }

    @Override
    public int updateAttachment(AttachmentDTO newImg) throws Exception {

        return clubDAO.updateAttachment(sqlSession, newImg);

    }

    @Override
    public int deleteAttachment(AttachmentDTO deletedImg) throws Exception {

        return clubDAO.deleteAttachment(sqlSession, deletedImg);

    }

    @Override
    public int insertAttachment(AttachmentDTO newImg) throws Exception {

        return clubDAO.insertAttachment(sqlSession, newImg);

    }

    @Override
    public List<AttachmentDTO> selectLogImg(int logNo) throws Exception {

        return clubDAO.selectLogImg(sqlSession, logNo);

    }

    @Override
    public int deleteLog(LogDTO logDTO) throws Exception {

        return clubDAO.deleteLog(sqlSession, logDTO);

    }

    @Override
    public int insertLogLike(LikeDTO likeDTO) throws Exception {

        return clubDAO.insertLogLike(sqlSession, likeDTO);

    }


    @Override
    public int deleteLogLike(LikeDTO likeDTO) throws Exception {

        return clubDAO.deleteLogLike(sqlSession, likeDTO);

    }

    @Override
    public int insertReplyLike(LikeDTO likeDTO) throws Exception {

        return clubDAO.insertReplyLike(sqlSession, likeDTO);

    }

    @Override
    public int deleteReplyLike(LikeDTO likeDTO) throws Exception {

        return clubDAO.deleteReplyLike(sqlSession, likeDTO);

    }



}
