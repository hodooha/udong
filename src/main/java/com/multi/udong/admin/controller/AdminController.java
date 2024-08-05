package com.multi.udong.admin.controller;

import com.multi.udong.admin.model.dto.ReportDTO;
import com.multi.udong.admin.service.AdminService;
import com.multi.udong.admin.service.ReportService;
import org.apache.ibatis.javassist.compiler.ast.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private URI uploadDir;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @Autowired
    private ReportService reportService;

    @GetMapping("/adminMain")
    public String adminMain(Model model) {
        model.addAttribute("members", adminService.getAllMembers());
        return "admin/adminMain";
    }
    @GetMapping("/memberSearch")
    public String searchMembers(@RequestParam(value = "search", required = false) String search, Model model) {
        if (search == null || search.trim().isEmpty()) {
            // 검색어가 없을 경우 기본 화면으로 리디렉션
            return "redirect:/admin/adminMain";
        }

        List<Member> members = adminService.searchMembersByIdOrNickname(search);
        model.addAttribute("members", members);
        return "admin/adminMain";
    }
    @GetMapping("/allreport")
    public String getAllReports(Model model) {
        List<ReportDTO> reports = reportService.getAllReports();  // 신고 리스트 가져오기
        model.addAttribute("reports", reports);
        return "admin/allreport";
    }
    @PostMapping("/changeReportStatus")
    @ResponseBody
    public ResponseEntity<?> changeReportStatus(@RequestBody Map<String, Object> payload) {
        int reportNo = Integer.parseInt(payload.get("reportNo").toString());
        String status = payload.get("status").toString();

        boolean success = reportService.changeReportStatus(reportNo, status);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false));
        }
    }

    @GetMapping("/downloadReportImage/{reportNo}")
    public ResponseEntity<Resource> downloadReportImage(@PathVariable Long reportNo) {
        ReportDTO report = reportService.getReportById(reportNo);

        if (report == null || report.getImageFileName() == null || report.getImageFileName().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(uploadDir).resolve(report.getImageFileName());
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getImageFileName() + "\"")
                        .body(resource);
            } else {
                System.err.println("File not found or not readable: " + filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}