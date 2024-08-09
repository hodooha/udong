package com.multi.udong.admin.controller;

import com.multi.udong.admin.model.dto.NoticeDTO;
import com.multi.udong.admin.model.dto.ReportDTO;
import com.multi.udong.admin.service.AdminService;
import com.multi.udong.admin.service.NoticeService;
import com.multi.udong.admin.service.ReportService;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    private NoticeService noticeService;

    @Value("${file.upload-dir:${user.dir}/src/main/resources/static/uploadFiles}")
    private String uploadDir;

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

        List<MemberDTO> members = adminService.searchMembersByIdOrNickname(search);
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
    public ResponseEntity<Resource> downloadReportImage(@PathVariable("reportNo") Long reportNo) {
        System.out.println("Downloading report image for report number: " + reportNo);

        ReportDTO report = reportService.getReportById(reportNo);

        if (report == null || report.getImageFileName() == null || report.getImageFileName().isEmpty()) {
            System.out.println("Report or image file name is null or empty");
            return ResponseEntity.notFound().build();
        }

        System.out.println("Image file name: " + report.getImageFileName());

        Path filePath = Paths.get(uploadDir).resolve(report.getImageFileName());
        System.out.println("Full file path: " + filePath.toString());

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                System.out.println("File exists and is readable");
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

    @GetMapping("/seller")
    public String showSellerManagement(Model model) {
        List<MemBusDTO> sellers = adminService.getAllSellers();

        for (MemBusDTO seller : sellers) {
            System.out.println("Seller ID: " + seller.getMemberNo());
            if (seller.getAttachmentDTO() != null) {
                System.out.println("  Attachment: " + seller.getAttachmentDTO().getSavedName());
            } else {
                System.out.println("  No attachment");
            }
        }

        model.addAttribute("sellers", sellers);
        return "admin/seller";
    }

    @PostMapping("/updateSellerStatus")
    @ResponseBody
    public ResponseEntity<?> updateSellerStatus(@RequestBody Map<String, Object> payload) {
        Integer memberNo = Integer.parseInt(payload.get("memberNo").toString());
        String status = payload.get("status").toString();

        boolean success = adminService.updateSellerStatus(memberNo, status);
        if (success) {
            MemBusDTO updatedSeller = adminService.getSellerByMemberNo(memberNo);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            if (updatedSeller != null) {
                response.put("approvedAt", updatedSeller.getApprovedAt());
            } else {
                response.put("approvedAt", null);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "상태 업데이트 실패"));
        }
    }

    @GetMapping("/downloadBusinessLicense/{memberNo}")
    public ResponseEntity<Resource> downloadBusinessLicense(@PathVariable("memberNo") Long memberNo) throws UnsupportedEncodingException {
        System.out.println("Downloading business license for member number: " + memberNo);

        AttachmentDTO attachment = adminService.getAttachmentByMemberNo(memberNo);
        if (attachment == null || attachment.getSavedName() == null || attachment.getSavedName().isEmpty()) {
            System.out.println("Attachment or file name is null or empty");
            return ResponseEntity.notFound().build();
        }

        System.out.println("File name: " + attachment.getSavedName());

        Path filePath = Paths.get(uploadDir).resolve(attachment.getSavedName());
        System.out.println("Full file path: " + filePath.toString());

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                System.out.println("File exists and is readable");
                String encodedFilename = URLEncoder.encode(attachment.getOriginalName(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
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

    @GetMapping("/blacklist")
    public String showBlacklist(Model model) {
        List<MemberDTO> blacklistRelatedMembers = adminService.getAllBlacklistRelatedMembers();
        model.addAttribute("blacklistedMembers", blacklistRelatedMembers);
        return "admin/blacklist";
    }

    @PostMapping("/blacklist/remove")
    @ResponseBody
    public String removeFromBlacklist(@RequestParam("memberNo") Integer memberNo) {
        adminService.removeMemberFromBlacklist(memberNo);
        return "success";
    }

    @PostMapping("/blacklist/add")
    @ResponseBody
    public String addToBlacklist(@RequestParam("memberNo") Integer memberNo) {
        adminService.addMemberToBlacklist(memberNo);
        return "success";
    }

    //공지사항
    @GetMapping("/notice")
    public String getAllNotices(Model model) {
        List<NoticeDTO> notices = noticeService.findAll();
        model.addAttribute("notices", notices);
        return "admin/notice";
    }

    @GetMapping("/notice/list")
    @ResponseBody
    public List<NoticeDTO> getNoticeList() {
        return noticeService.findAll();
    }

    @PostMapping("/notice/insert")
    public String insertNotice(@ModelAttribute NoticeDTO notice,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal UserDetails userDetails) {
        noticeService.save(notice);
        redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
        return "redirect:/admin/notice";
    }

    @GetMapping("/noticeInsertForm")
    public String showNoticeInsertForm() {
        return "admin/noticeInsertForm";
    }
    @GetMapping("/detail/{noticeNo}")
    public String getNoticeDetail(@PathVariable("noticeNo") int noticeNo, Model model) {
        NoticeDTO notice = noticeService.findById(noticeNo);
        model.addAttribute("notice", notice);
        return "admin/noticeDetail"; // noticeDetail.html 파일을 반환
    }

}