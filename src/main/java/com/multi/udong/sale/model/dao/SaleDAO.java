package com.multi.udong.sale.model.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.multi.udong.sale.model.dto.SaleDTO;

@Repository
public class SaleDAO {
    @Autowired
    private SqlSession sqlSession;

    public void insertSale(SaleDTO saleDTO) {
        sqlSession.insert("Sale.insertSale", saleDTO);
    }
}