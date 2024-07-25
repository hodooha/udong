package com.multi.udong.sale.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.sale.model.dao.SaleDAO;
import com.multi.udong.sale.model.dto.SaleDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {
    //SaleService 인터페이스를 구현하는 class
    @Autowired
    private SaleDAO saleDAO; //데이터 접근
    @Autowired
    private SqlSessionTemplate sqlSession; //MyBatis와 스프링을 통합하는 템플릿 SQL 세션을 관리하고, 데이터베이스 쿼리를 실행


    @Transactional
    public void insertSale(SaleDTO saleDTO, List<AttachmentDTO> imgList) throws Exception {
        int saleNo = saleDAO.insertSale(sqlSession, saleDTO);
        for (AttachmentDTO attachment : imgList) {
            attachment.setTypeCode("SAL"); //첨부파일의 타입코드 설정
            attachment.setTargetNo(saleNo); //첨부파일의 대상 No 설정
        }

        saleDAO.insertAttachments(sqlSession, imgList);
    }

    @Override
    public List<SaleDTO> getAllSalesWithAttachments() {
        List<SaleDTO> sales = saleDAO.getAllSalesWithAttachments(sqlSession);
        for (SaleDTO sale : sales) {
            if (sale.getAttachments() == null) {
                sale.setAttachments(new ArrayList<>());
            }
        }
        return sales;
    }
    @Override
    public List<SaleDTO> search(String keyword) {
        return saleDAO.search(sqlSession, keyword);
    }
}