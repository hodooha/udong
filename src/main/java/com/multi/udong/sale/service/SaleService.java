package com.multi.udong.sale.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.sale.model.dto.SaleDTO;

import java.util.List;

public interface SaleService {
    void insertSale(SaleDTO saleDTO, List<AttachmentDTO> imgList) throws Exception;

    List<SaleDTO> getAllSales();
}