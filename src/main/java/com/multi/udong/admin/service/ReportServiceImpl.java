package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dao.AdminMapper;
import com.multi.udong.admin.model.dao.ReportDAO;
import com.multi.udong.admin.model.dto.ReportDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO;
    private final AdminMapper adminMapper;

    @Autowired
    public ReportServiceImpl(ReportDAO reportDAO, AdminMapper adminMapper) {
        this.reportDAO = reportDAO;
        this.adminMapper = adminMapper;
    }

    @Override
    public void saveReport(ReportDTO report) {
        reportDAO.insertReport(report);
    }

    @Override
    public List<ReportDTO> getAllReports() {
        return reportDAO.findAllReports();
    }

    @Override
    public boolean changeReportStatus(int reportNo, String status) {
        try {
            ReportDTO report = reportDAO.findReportById(reportNo);
            int reportedMemberNo = report.getReportedMember();

            // 상태가 'A' (승인)인 경우 신고당한 회원의 신고 횟수 증가 및 블랙리스트 처리
            if ("A".equals(status)) {
                MemberDTO member = adminMapper.findMemberByNo(reportedMemberNo);
                int currentReportedCnt = member.getReportedCnt();

                if (currentReportedCnt < 3) {
                    adminMapper.incrementReportedCnt(reportedMemberNo);

                    if (currentReportedCnt + 1 >= 3) {
                        adminMapper.blacklistMember(reportedMemberNo);
                    }
                }
            }

            // 상태가 'R' (반려)로 바뀌면 신고된 회원의 신고 횟수 감소 및 블랙리스트 해제 처리
            else if ("R".equals(status)) {
                MemberDTO member = adminMapper.findMemberByNo(reportedMemberNo);
                int currentReportedCnt = member.getReportedCnt();

                if (currentReportedCnt > 0) {
                    adminMapper.decrementReportedCnt(reportedMemberNo);

                    if (currentReportedCnt - 1 < 3) {
                        adminMapper.unblacklistMember(reportedMemberNo);
                    }
                }
            }

            // 기존 신고 상태 업데이트
            reportDAO.updateReportStatus(reportNo, status);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public ReportDTO getReportById(Long reportNo) {
        return reportDAO.findReportById(reportNo);
    }
}