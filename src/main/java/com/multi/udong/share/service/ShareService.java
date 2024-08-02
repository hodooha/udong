package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dto.*;

import java.util.List;

/**
 * 대여 및 나눔 Service interface
 *
 * @author 하지은
 * @since 2024 -07-21
 */
public interface ShareService {
    /**
     * 물건 카테고리 조회
     *
     * @return the sha cat
     * @throws Exception the exception
     * @since 2024 -07-21
     */
    List<ShaCatDTO> getShaCat() throws Exception;

    /**
     * 물건 등록 (물건 정보 및 사진)
     *
     * @param itemDTO the item dto
     * @param imgList the img list
     * @throws Exception the exception
     * @since 2024 -07-21
     */
    void insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception;


    ShaItemDTO getItemDetailWithViewCnt(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception;

    /**
     * 물건 상세 정보 조회 (물건 정보 & 사진목록)
     *
     * @param itemDTO the item dto
     * @return the item detail
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    ShaItemDTO getItemDetail(ShaItemDTO itemDTO) throws Exception;

    /**
     * 물건 목록 조회 (검색)
     *
     * @param criteriaDTO the criteria dto
     * @return the sha item result dto
     * @throws Exception the exception
     * @since 2024 -07-24
     */
    ShaItemResultDTO searchItems(ShaCriteriaDTO criteriaDTO) throws Exception;

    /**
     * 물건 총 개수 조회
     *
     * @param criteriaDTO the criteria dto
     * @return the item counts
     * @throws Exception the exception
     */
    int getItemCounts(ShaCriteriaDTO criteriaDTO) throws Exception;

    /**
     * 대여 및 나눔 신청
     *
     * @param reqDTO the req dto
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    void insertRequest(ShaReqDTO reqDTO) throws Exception;

    /**
     * 기존 대여 및 나눔 신청 내역 조회
     *
     * @param reqDTO the req dto
     * @return the sha req dto
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    ShaReqDTO findRequest(ShaReqDTO reqDTO) throws Exception;


    /**
     * 물건 수정 (파일 포함)
     *
     * @param itemDTO    the item dto
     * @param newImgList the new img list
     * @param delImgList the del img list
     * @throws Exception the exception
     * @since 2024 -07-30
     */
    void updateItem(ShaItemDTO itemDTO, List<AttachmentDTO> newImgList, List<AttachmentDTO> delImgList) throws Exception;


    /**
     * 물건 삭제
     *
     * @param target the target
     * @param c      the c
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    List<AttachmentDTO> deleteItem(ShaItemDTO target, CustomUserDetails c) throws Exception;

    /**
     * 물건 상태 업데이트
     *
     * @param itemDTO the item dto
     * @param c
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    void updateItStat(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception;


    /**
     * 찜 등록 및 삭제
     *
     * @param likeDTO the like dto
     * @throws Exception the exception
     * @since 2024 -08-01
     */
    void updateShaLike(ShaLikeDTO likeDTO) throws Exception;


    ShaDreamResultDTO getLendList(ShaDreamCriteriaDTO criteriaDTO) throws Exception;

    int getLendCounts(ShaDreamCriteriaDTO criteriaDTO) throws Exception;
}
