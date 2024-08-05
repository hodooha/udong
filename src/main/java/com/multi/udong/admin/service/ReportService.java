package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    void saveReport(ReportDTO report);
    List<ReportDTO> getAllReports();
    boolean changeReportStatus(int reportNo, String status);
    ReportDTO getReportById(Long reportNo);
}