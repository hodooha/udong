package com.multi.udong.sale.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.sale.model.dto.SaleDTO;

import java.util.List;

/**
 * The interface Sale service.
 *
 * @author 윤정해
 * @since 2024 -07-24
 */
public interface SaleService {
    /**
     * Insert sale.
     *
     * @param saleDTO the sale dto
     * @param imgList the img list
     * @throws Exception the exception
     * @since 2024 -07-24
     */
    void insertSale(SaleDTO saleDTO, List<AttachmentDTO> imgList) throws Exception;
                                    //판매 정보와 첨부 이미지 리스트를 받아 저장하는 메소드
    /**
     * Gets all sales.
     *
     * @return the all sales
     */

    List<SaleDTO> getAllSalesWithAttachments(); //모든 판매정보 조회 메서드

}