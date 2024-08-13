package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dto.*;

import java.util.List;

public interface ShareService {
    List<ShaCatDTO> getShaCat() throws Exception;

    void insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception;

    public ShaItemDTO getItemDetailWithViewCnt(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception;

    ShaItemDTO getItemDetail(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception;

    ShaItemResultDTO searchItems(ShaCriteriaDTO criteriaDTO) throws Exception;

    int getItemCounts(ShaCriteriaDTO criteriaDTO) throws Exception;

    void insertRequest(ShaReqDTO reqDTO) throws Exception;

    ShaReqDTO findRequest(ShaReqDTO reqDTO) throws Exception;

    void updateItem(ShaItemDTO itemDTO, List<AttachmentDTO> newImgList, List<AttachmentDTO> delImgList) throws Exception;

    void deleteItem(ShaItemDTO target, CustomUserDetails c) throws Exception;

    void updateItStat(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception;

    ShaItemDTO updateShaLike(ShaLikeDTO likeDTO, CustomUserDetails c) throws Exception;

    ShaDreamResultDTO getLendList(ShaDreamCriteriaDTO criteriaDTO) throws Exception;

    List<ShaReqDTO> getRequesters(ShaReqDTO itemNo) throws Exception;

    void approveReq(ShaReqDTO reqDTO) throws Exception;

    void evalWithReturnReq(ShaEvalDTO evalDTO, CustomUserDetails c) throws Exception;

    ShaDreamResultDTO getBorrowList(ShaDreamCriteriaDTO criteriaDTO) throws Exception;

    void deleteReq(ShaReqDTO shaReqDTO, CustomUserDetails c) throws Exception;

    void evalWithEndReq(ShaEvalDTO evalDTO, CustomUserDetails c) throws Exception;

    void insertReport(ShaReportDTO reportDTO, CustomUserDetails c) throws Exception;

    List<ShaItemDTO> recommendItem(CustomUserDetails c) throws Exception;
}
