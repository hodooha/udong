package com.multi.udong.club.service;

import com.multi.udong.club.model.dto.*;
import com.multi.udong.common.model.dto.AttachmentDTO;

import java.util.List;

/**
 * 모임 service interface
 *
 * @author 강성현
 * @since 2024 -07-25
 */
public interface ClubService {

    List<CategoryDTO> selectCategoryList() throws Exception;

    int insertClub(ClubDTO clubDTO) throws Exception;

    List<ClubDTO> selectClubList(FilterDTO filterDTO) throws Exception;

    int selectClubCount(FilterDTO filterDTO) throws Exception;

    ClubDTO selectClubHome(RequestDTO requestDTO) throws Exception;

    int requestJoinClub(RequestDTO requestDTO) throws Exception;

    String checkJoinStatus(RequestDTO requestDTO) throws Exception;

    ClubDTO checkPersonnel(int clubNo) throws Exception;

    int checkClubMaster(int clubNo) throws Exception;

    int cancelJoinRequest(RequestDTO requestDTO) throws Exception;

    int leaveClub(RequestDTO requestDTO) throws Exception;

    int deleteClub(RequestDTO requestDTO) throws Exception;

    int reportClub(ReportDTO reportDTO) throws Exception;

    int updateClub(ClubDTO clubDTO) throws Exception;

    List<AttachmentDTO> selectClubImg(int clubNo) throws Exception;

    int insertLog(LogDTO logDTO) throws Exception;

    List<LogDTO> selectLogList(FilterDTO filterDTO) throws Exception;

    int selectLogCount(FilterDTO filterDTO) throws Exception;

    LogDTO selectLogDetail(RequestDTO requestDTO) throws Exception;

    int insertReply(ReplyDTO replyDTO) throws Exception;

    int checkReplyWriter(int replyNo) throws Exception;

    int updateReply(ReplyDTO replyDTO) throws Exception;

    int deleteReply(ReplyDTO replyDTO) throws Exception;

    int checkLogWriter(int logNo) throws Exception;

    int reportLog(ReportDTO reportDTO) throws Exception;

    int updateLog(LogDTO logDTO) throws Exception;

    AttachmentDTO selectAttachment(int fileNo) throws Exception;

    int updateAttachment(AttachmentDTO newImg) throws Exception;

    int deleteAttachment(AttachmentDTO deletedImg) throws Exception;

    int insertAttachment(AttachmentDTO newImg) throws Exception;

    List<AttachmentDTO> selectLogImg(int logNo) throws Exception;

    int deleteLog(LogDTO logDTO) throws Exception;

    int insertLogLike(LikeDTO likeDTO) throws Exception;

    int deleteLogLike(LikeDTO likeDTO) throws Exception;

    int insertReplyLike(LikeDTO likeDTO) throws Exception;

    int deleteReplyLike(LikeDTO likeDTO) throws Exception;

    int addLogViews(int logNo) throws Exception;

    String checkIsClubDeleted(int clubNo) throws Exception;

    String checkIsLogDeleted(int logNo) throws Exception;

    List<ScheduleDTO> selectScheduleList(FilterDTO filterDTO) throws Exception;

    int selectScheduleCount(FilterDTO filterDTO) throws Exception;

    int insertSchedule(ScheduleDTO scheduleDTO) throws Exception;

    String checkIsScheduleDeleted(int scheduleNo) throws Exception;

    ScheduleDTO selectScheduleDetail(RequestDTO requestDTO) throws Exception;

    ScheduleDTO checkSchedulePersonnel(int scheduleNo) throws Exception;

    int joinSchedule(RequestDTO requestDTO) throws Exception;

    int checkScheduleJoinStatus(RequestDTO requestDTO) throws Exception;

    int cancelJoinSchedule(RequestDTO requestDTO) throws Exception;

    int checkScheduleMaker(int scheduleNo) throws Exception;

    int deleteSchedule(RequestDTO requestDTO) throws Exception;

    List<AttachmentDTO> selectAlbumList(FilterDTO filterDTO) throws Exception;

    int selectAlbumCount(FilterDTO filterDTO) throws Exception;
}
