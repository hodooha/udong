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

    String checkAdmin(int memberNo) throws Exception;

    List<LogDTO> selectLogList(FilterDTO filterDTO) throws Exception;

    int selectLogCount(FilterDTO filterDTO) throws Exception;
}
