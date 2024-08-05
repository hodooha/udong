package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dto.*;

import java.util.List;

public interface ShareService {
    List<ShaCatDTO> getShaCat() throws Exception;

    void insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception;

    void plusViewCnt(ShaItemDTO itemDTO) throws Exception;

    ShaItemDTO getItemDetail(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception;

    ShaItemResultDTO searchItems(ShaCriteriaDTO criteriaDTO) throws Exception;

    int getItemCounts(ShaCriteriaDTO criteriaDTO) throws Exception;

    void insertRequest(ShaReqDTO reqDTO) throws Exception;

    ShaReqDTO findRequest(ShaReqDTO reqDTO) throws Exception;


    void updateItem(ShaItemDTO itemDTO, List<AttachmentDTO> newImgList, List<AttachmentDTO> delImgList) throws Exception;


    List<AttachmentDTO> deleteItem(ShaItemDTO target, CustomUserDetails c) throws Exception;

    void updateItStat(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception;


    void updateShaLike(ShaLikeDTO likeDTO) throws Exception;


    ShaDreamResultDTO getLendList(ShaDreamCriteriaDTO criteriaDTO) throws Exception;

    List<ShaReqDTO> getRequesters(ShaReqDTO itemNo) throws Exception;
}
