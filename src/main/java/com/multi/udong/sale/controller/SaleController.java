package com.multi.udong.sale.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.sale.model.dto.SaleDTO;
import com.multi.udong.sale.service.SaleService;
import com.multi.udong.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/sale") //sale에 대한 경로 요청 처리
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("/saleMain")
    public String saleMain(Model model,
                           @RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "excludeExpired", required = false) Boolean excludeExpired,
                           @RequestParam(value = "sortOption", required = false, defaultValue = "latest") String sortOption) {
        List<SaleDTO> sales = saleService.getSales(search, excludeExpired, sortOption);
        model.addAttribute("sales", sales);
        return "sale/saleMain";
    }

    @GetMapping("/saleInsertForm")
    public String saleInsertForm() { //땡처리 작성 폼 페이지 반환
        return "sale/saleInsertForm";
    }

    @PostMapping("/insert")
    public String insertSale(@ModelAttribute SaleDTO saleDTO, @RequestParam("imageFiles") List<MultipartFile> imageFiles,
                             @RequestParam(value = "startedAtCombined", required = false) String startedAtCombined,
                             @RequestParam(value = "endedAtCombined", required = false) String endedAtCombined,
                             @AuthenticationPrincipal CustomUserDetails member, Model model) {

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
                return "redirect:/error?message=DateParsingError";
            }
        }

        try {
            List<AttachmentDTO> imgList = new ArrayList<>();
            String imgPath = new File("src/main/resources/static/uploadFiles").getAbsolutePath();

            if (!imageFiles.isEmpty()) {
                File mkdir = new File(imgPath);
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
                    f.transferTo(new File(imgPath + "\\" + savedName));

                    if (saleDTO.getImagePath() == null) {
                        saleDTO.setImagePath("/uploadFiles/" + savedName);
                    }
                }
            }

            saleService.insertSale(saleDTO, imgList); // 땡처리 정보와 이미지 저장

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving file: " + e.getMessage());
            return "redirect:/error?message=FileSaveError";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
            return "redirect:/error?message=UnexpectedError";
        }

        return "redirect:/sale/saleMain"; // 모든 처리가 완료되면 땡처리 메인으로 리다이렉트
    }

}