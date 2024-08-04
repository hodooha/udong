package com.multi.udong.admin.model.dao;

import com.multi.udong.admin.model.dto.ReportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportDAO {
    void insertReport(ReportDTO report);
    List<ReportDTO> findAllReports();
    void updateReportStatus(@Param("reportNo") int reportNo, @Param("status") String status);
    ReportDTO findReportById(Long reportNo);

    ReportDTO findReportById(int reportNo);
}