package com.multi.udong.sale.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.sale.model.dao.SaleDAO;
import com.multi.udong.sale.model.dto.SaleDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleDAO saleDAO;

    @Autowired
    private SqlSessionTemplate sqlSession;


    @Transactional
    public void insertSale(SaleDTO saleDTO, List<AttachmentDTO> imgList) throws Exception {
        int saleNo = saleDAO.insertSale(sqlSession, saleDTO);
        for (AttachmentDTO attachment : imgList) {
            attachment.setTypeCode("SAL");
            attachment.setTargetNo(saleNo);
        }

        saleDAO.insertAttachments(sqlSession, imgList);
    }

    @Override
    public List<SaleDTO> getAllSales() {
        List<SaleDTO> sales = saleDAO.getAllSales(sqlSession);
        System.out.println("Number of sales retrieved: " + sales.size());
        for (SaleDTO sale : sales) {
            System.out.println("Sale: " + sale.getTitle());
        }
        return sales;


    }
}