package com.multi.udong.sale.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.sale.model.dao.SaleDAO;
import com.multi.udong.sale.model.dto.SaleDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
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
    public List<SaleDTO> getAllActiveWithAttachments() {
        return saleDAO.getAllActiveWithAttachments(sqlSession);
    }

    @Override
    public List<SaleDTO> search(String keyword, Boolean excludeExpired) {
        return saleDAO.search(sqlSession, keyword, excludeExpired);
    }

    @Override
    public List<SaleDTO> getSales(String search, Boolean excludeExpired, String sortOption) {
        List<SaleDTO> sales;
        if (search == null || search.isEmpty()) {
            if (Boolean.TRUE.equals(excludeExpired)) {
                sales = getAllActiveWithAttachments();
            } else {
                sales = getAllSalesWithAttachments();
            }
        } else {
            sales = search(search, excludeExpired != null ? excludeExpired : false);
        }
        switch (sortOption) {
            case "deadline":
                sales.sort(Comparator.comparing(sale ->
                        Duration.between(sale.getStartedAt(), sale.getEndedAt())));
                break;
            case "lowPrice":
                sales.sort(Comparator.comparing(SaleDTO::getSalePrice));
                break;
            default: // "latest"
                sales.sort(Comparator.comparing(SaleDTO::getStartedAt).reversed());
        }
        return sales;
    }
    @Override
    public SaleDTO getSaleById(int saleNo) {
        return saleDAO.getSaleById(sqlSession, saleNo); // DAO 메서드 호출
    }
    @Override
    public void incrementViews(int saleNo) {
        saleDAO.incrementViews(saleNo);
    }

    @Override
    public SaleDTO getSaleWithAttachments(int saleNo) {
        SaleDTO sale = saleDAO.getSaleById(sqlSession, saleNo);
        if (sale != null) {
            List<AttachmentDTO> attachments = saleDAO.getAttachmentsBySaleNo(sqlSession, saleNo);
            sale.setAttachments(attachments);

            // imagePath 설정 (첫 번째 첨부파일의 경로를 사용)
            if (!attachments.isEmpty()) {
                sale.setImagePath("/uploadFiles/" + attachments.get(0).getSavedName());
            }
        }
        return sale;
    }
    @Override
    @Transactional
    public void deleteSale(int saleNo) throws Exception {
        saleDAO.deleteSale(saleNo);
    }
}
