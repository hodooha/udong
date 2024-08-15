package com.multi.udong.admin.controller;

import com.multi.udong.admin.model.dto.NoticeDTO;
import com.multi.udong.admin.model.dto.ReportDTO;
import com.multi.udong.admin.service.AdminService;
import com.multi.udong.admin.service.NoticeService;
import com.multi.udong.admin.service.ReportService;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    private NoticeService noticeService;

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


        Path filePath = Paths.get(System.getProperty("user.home"), "udongUploads").resolve(report.getImageFileName());
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


        Path filePath = Paths.get(System.getProperty("user.home"), "udongUploads").resolve(attachment.getSavedName());
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
    @ResponseBody
    public ResponseEntity<?> insertNotice(@ModelAttribute NoticeDTO noticeDTO,
                                          @RequestParam("imageFile") MultipartFile imageFile,
                                          @AuthenticationPrincipal CustomUserDetails member) {
        try {
            noticeDTO.setWriter(member.getMemberDTO().getMemberNo());

            AttachmentDTO attachmentDTO = null;

            if (!imageFile.isEmpty()) {
                String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
                String savePath = path + File.separator;

                File mkdir = new File(savePath);
                if (!mkdir.exists()) {
                    mkdir.mkdirs();
                }

                String originalFileName = imageFile.getOriginalFilename();
                String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                attachmentDTO = new AttachmentDTO();
                attachmentDTO.setOriginalName(originalFileName);
                attachmentDTO.setSavedName(savedName);
                attachmentDTO.setTypeCode("NOTI");
                attachmentDTO.setFileUrl("/udongUploads/" + savedName);

                imageFile.transferTo(new File(savePath + savedName));

                noticeDTO.setImagePath("/udongUploads/" + savedName);
            }

            int noticeNo = noticeService.saveNoticeWithAttachment(noticeDTO, attachmentDTO);
            System.out.println("Saved notice no: " + noticeNo);
            System.out.println("Saved image path: " + noticeDTO.getImagePath());

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "type", "success",
                    "message", "공지사항이 성공적으로 등록되었습니다.",
                    "noticeNo", noticeNo
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "type", "error",
                    "message", "파일 업로드 중 오류가 발생했습니다."
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "type", "error",
                    "message", "알 수 없는 오류가 발생했습니다."
            ));
        }
    }


    @GetMapping("/noticeInsertForm")
    public String showInsertForm(Model model) {
        // 새 NoticeDTO 객체를 생성하여 모델에 추가
        model.addAttribute("notice", new NoticeDTO());
        return "admin/noticeInsertForm";
    }
    @GetMapping("/detail/{noticeNo}")
    public String getNoticeDetail(@PathVariable("noticeNo") int noticeNo, Model model) {
        NoticeDTO notice = noticeService.findById(noticeNo);
        if (notice == null) {
            return "redirect:/error";  // 또는 적절한 에러 페이지로 리다이렉트
        }
        model.addAttribute("notice", notice);
        System.out.println("Image Path: " + notice.getImagePath());
        return "admin/noticeDetail";
    }
    // 수정 폼
    @GetMapping("/noticeUpdate")
    public String showUpdateForm(@RequestParam("noticeNo") int noticeNo, Model model) {
        NoticeDTO notice = noticeService.getNoticeWithAttachments(noticeNo);
        if (notice == null) {
            return "redirect:/errorPage"; // 오류 페이지로 리디렉션
        }
        model.addAttribute("notice", notice);
        return "admin/noticeUpdateForm";
    }

    // 수정
    @PostMapping("/notice/update")
    @ResponseBody
    public ResponseEntity<?> updateNotice(@ModelAttribute NoticeDTO notice,
                                          @RequestParam(value = "file", required = false) MultipartFile file,
                                          @RequestParam(value = "deleteImage", required = false) Boolean deleteImage) {
        try {
            // 현재 시간 가져오기
            LocalDateTime now = LocalDateTime.now();

            // 날짜 유효성 검사
            if (notice.getStartedAt().isBefore(now)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "시작일은 현재 시간 이후여야 합니다."
                ));
            }
            if (notice.getEndedAt().isBefore(notice.getStartedAt())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "종료일은 시작일 이후여야 합니다."
                ));
            }

            AttachmentDTO attachmentDTO = null;

            if (deleteImage != null && deleteImage) {
                // 기존 이미지 삭제 로직
                notice.setImagePath(null);
            } else if (file != null && !file.isEmpty()) {
                // 새 이미지 업로드 로직

                String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
                String savePath = path + File.separator;

                File mkdir = new File(savePath);
                if (!mkdir.exists()) {
                    mkdir.mkdirs();
                }

                String originalFileName = file.getOriginalFilename();
                String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                attachmentDTO = new AttachmentDTO();
                attachmentDTO.setOriginalName(originalFileName);
                attachmentDTO.setSavedName(savedName);
                attachmentDTO.setTypeCode("NOTI");
                attachmentDTO.setFileUrl("/udongUploads/" + savedName);

                file.transferTo(new File(savePath + savedName));
                notice.setImagePath("/udongUploads/" + savedName);
            }

            noticeService.updateNotice(notice, attachmentDTO);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "공지사항이 성공적으로 수정되었습니다.",
                    "noticeNo", notice.getNoticeNo()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "공지사항 수정 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // 삭제 처리
    @PostMapping("/deleteNotice")
    public String deleteNotice(@RequestParam("noticeNo") int noticeNo) {
        noticeService.delete(noticeNo);
        return "redirect:/admin/notice";
    }

}