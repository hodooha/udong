package com.multi.udong.sale.service;

import com.multi.udong.sale.model.dao.SaleDAO;
import com.multi.udong.sale.model.dto.SaleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class SaleServiceImpl implements SaleService {

    private final SaleDAO saleDAO;

    @Autowired
    public SaleServiceImpl(SaleDAO saleDAO) {
        this.saleDAO = saleDAO;
    }

    @Override
    public void insertSale(SaleDTO saleDTO) {
        saleDAO.insertSale(saleDTO);
    }
}