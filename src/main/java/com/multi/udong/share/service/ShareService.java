package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaCriteriaDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;

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
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-21
     */
    int insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception;

    /**
     * 대여 물건 목록 조회
     *
     * @param locCode the loc code
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-22
     */
    List<ShaItemDTO> rentItemList(int locCode) throws Exception;

    /**
     * 나눔 물건 목록 조회
     *
     * @param locCode the loc code
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-22
     */
    List<ShaItemDTO> giveItemList(int locCode) throws Exception;

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
     * 물건 검색
     *
     * @param criteriaDTO the criteria dto
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-24
     */
    List<ShaItemDTO> searchItems(ShaCriteriaDTO criteriaDTO) throws Exception;


    /**
     * 물건 총 개수 조회
     *
     * @param criteriaDTO the criteria dto
     * @return the item counts
     * @throws Exception the exception
     */
    int getItemCounts(ShaCriteriaDTO criteriaDTO) throws Exception;
}
