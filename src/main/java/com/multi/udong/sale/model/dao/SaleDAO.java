package com.multi.udong.sale.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.sale.model.dto.SaleDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleDAO {

    public int insertSale(SqlSessionTemplate sqlSession, SaleDTO saleDTO) throws Exception{
        sqlSession.insert("SaleMapper.insertSale", saleDTO);
        return saleDTO.getSaleNo();
    }
    public int insertAttachments(SqlSessionTemplate sqlSession, List<AttachmentDTO> imgList) {
        return sqlSession.insert("SaleMapper.insertAttachments", imgList);
    }
    public List<SaleDTO> getAllSales(SqlSessionTemplate sqlSession) {
        return sqlSession.selectList("SaleMapper.getAllSales");
    }

}