package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dto.ReportDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public void saveReport(ReportDTO report) {
        sqlSession.insert("ReportMapper.insertReport", report);
    }
}