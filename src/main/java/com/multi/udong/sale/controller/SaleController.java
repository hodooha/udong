package com.multi.udong.sale.controller;

import com.multi.udong.admin.model.dto.ReportDTO;
import com.multi.udong.admin.service.ReportService;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.sale.model.dto.SaleDTO;
import com.multi.udong.sale.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/sale") //sale에 대한 경로 요청 처리
public class SaleController {

    @Autowired
    private final SaleService saleService;
    @Autowired
    private ReportService reportService;


    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("/saleMain")
    public String saleMain(Model model,
                           @RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "excludeExpired", required = false) Boolean excludeExpired,
                           @RequestParam(value = "sortOption", required = false, defaultValue = "latest") String sortOption,
                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {

            if (customUserDetails == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }


            List<SaleDTO> sales = saleService.getSales(search, excludeExpired, sortOption);
            model.addAttribute("sales", sales);
            return "sale/saleMain";
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/saleInsertForm")
    public String saleInsertForm() { //땡처리 작성 폼 페이지 반환
        return "sale/saleInsertForm";
    }

    @PostMapping("/insert")
    public String insertSale(@ModelAttribute SaleDTO saleDTO,
                             @RequestParam("imageFiles") List<MultipartFile> imageFiles,
                             @RequestParam(value = "startedAtCombined", required = false) String startedAtCombined,
                             @RequestParam(value = "endedAtCombined", required = false) String endedAtCombined,
                             @AuthenticationPrincipal CustomUserDetails member,
                             Model model) {

        saleDTO.setLocationCode(member.getMemberDTO().getMemAddressDTO().getLocationCode());
        saleDTO.setWriter(member.getMemberDTO().getMemberNo());

        if (startedAtCombined != null && endedAtCombined != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime startedAt = LocalDateTime.parse(startedAtCombined, formatter);
                LocalDateTime endedAt = LocalDateTime.parse(endedAtCombined, formatter);

                saleDTO.setStartedAt(startedAt);
                saleDTO.setEndedAt(endedAt);
                System.out.println("StartedAt set to: " + startedAt);
                System.out.println("EndedAt set to: " + endedAt);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing dates: " + e.getMessage());
                return "redirect:/common/errorPage?message=DateParsingError";
            }
        }

        try {
            List<AttachmentDTO> imgList = new ArrayList<>();
            String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
            String savePath = path + File.separator;

            if (!imageFiles.isEmpty()) {
                File mkdir = new File(savePath);
                if (!mkdir.exists()) {
                    mkdir.mkdirs();
                }
                for (MultipartFile f : imageFiles) {
                    String originalFileName = f.getOriginalFilename();
                    String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                    AttachmentDTO img = new AttachmentDTO();
                    img.setOriginalName(originalFileName);
                    img.setSavedName(savedName);
                    img.setTypeCode(String.valueOf(saleDTO.getSaleNo()));

                    imgList.add(img);
                    f.transferTo(new File(savePath + savedName));

                    if (saleDTO.getImagePath() == null) {
                        saleDTO.setImagePath("/udongUploads/" + savedName);
                    }
                }
            }

            saleService.insertSale(saleDTO, imgList); // 땡처리 정보와 이미지 저장

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving file: " + e.getMessage());
            return "redirect:/common/errorPage?message=FileSaveError";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
            return "redirect:/common/errorPage?message=UnexpectedError";
        }

        return "redirect:/sale/saleMain"; // 모든 처리가 완료되면 땡처리 메인으로 리다이렉트
    }

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @GetMapping("/detail/{saleNo}")
    public String saleDetail(@PathVariable("saleNo") int saleNo, Model model, Authentication authentication) {
        SaleDTO sale = saleService.getSaleWithAttachments(saleNo);
        saleService.incrementViews(saleNo);
        System.out.println("Sale: " + sale); // 로그 추가
        System.out.println("Created At: " + sale.getCreatedAt()); // 로그 추가
        System.out.println("Views: " + sale.getViews()); // 로그 추가
        model.addAttribute("sale", sale);
        model.addAttribute("kakaoApiKey", kakaoApiKey);


        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                model.addAttribute("currentUserId", userDetails.getUsername());


                boolean isSeller = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"));
                model.addAttribute("isSeller", isSeller);
            }
        }

        return "sale/saleDetail";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteSale")
    public String deleteSale(@RequestParam("saleNo") int saleNo, RedirectAttributes redirectAttributes) {
        try {
            saleService.deleteSale(saleNo);
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertMessage", "성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertMessage", "삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/sale/saleMain";
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/saleReport")
    public String showSaleReportForm(@RequestParam("saleNo") int saleNo,
                                     Model model,
                                     @AuthenticationPrincipal CustomUserDetails member) {
        SaleDTO sale = saleService.getSaleById(saleNo);

        if (member != null) {
            MemberDTO memberDTO = member.getMemberDTO();
            model.addAttribute("currentUser", memberDTO);
        }

        model.addAttribute("sale", sale);
        return "sale/saleReport";
    }

    @PostMapping("/saleReport")
    public String submitSaleReport(@RequestParam("title") String title,
                                   @RequestParam("reason") String reason,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("saleNo") int saleNo,
                                   @AuthenticationPrincipal CustomUserDetails member,
                                   RedirectAttributes redirectAttributes) {

        try {
            SaleDTO sale = saleService.getSaleById(saleNo);
            ReportDTO report = new ReportDTO();
            report.setTypeCode("SAL");
            report.setReportedNo(saleNo);
            report.setReportedMember(sale.getWriter());
            report.setReporterMember(member.getMemberDTO().getMemberNo());
            report.setReason(reason);
            report.setUrl("/sale/detail/" + saleNo);
            report.setStatus("W");
            report.setCreatedAt(LocalDateTime.now());

            if (!file.isEmpty()) {
                String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
                String savePath = path + File.separator;

                File mkdir = new File(savePath);
                if (!mkdir.exists()) {
                    mkdir.mkdirs();
                }
                String originalFileName = file.getOriginalFilename();
                String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                file.transferTo(new File(savePath + savedName));
                report.setImageFileName(savedName);
                report.setUrl(report.getUrl() + "?file=" + savedName);
            }

            reportService.saveReport(report);

            return "redirect:/sale/saleMain";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/common/errorPage?message=UnexpectedError";
        }
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping("/updateStatus/{saleNo}")
    public ResponseEntity<?> updateSaleStatus(@PathVariable("saleNo") int saleNo,
                                              @RequestBody Map<String, String> body,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            String status = body.get("status");
            saleService.updateSaleStatus(saleNo, status, currentUser.getMemberDTO().getMemberNo());

            return ResponseEntity.ok()
                    .body(Map.of("status", status,
                            "message", "판매 상태가 성공적으로 업데이트되었습니다."));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "권한 없음",
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류",
                            "message", "판매 상태 업데이트 중 오류가 발생했습니다."));
        }
    }
}