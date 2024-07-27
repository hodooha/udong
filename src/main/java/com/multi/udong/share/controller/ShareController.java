package com.multi.udong.share.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.security.CustomUserDetails;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaCriteriaDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import com.multi.udong.share.model.dto.ShaPageDTO;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 대여 및 나눔 Controller
 *
 * @author 하지은
 * @since 2024 -07-21
 */
@RequiredArgsConstructor
@RequestMapping("/share")
@Controller
public class ShareController {

    private final ShareService shareService;


    /**
     * 대여 메인페이지 이동
     *
     * @return the string
     * @since 2024 -07-26
     */
    @GetMapping("/rent")
    public String rentMain() {
        return "share/rentMain";
    }


    /**
     * 나눔 메인페이지 이동
     *
     * @return the string
     * @since 2024 -07-26
     */
    @GetMapping("/give")
    public String giveMain() {
        return "share/giveMain";
    }

    /**
     * 카테고리 목록 조회
     *
     * @return the list
     * @since 2024 -07-26
     */
    @GetMapping("/getCatList")
    @ResponseBody
    public List<ShaCatDTO> getCatList() {
        List<ShaCatDTO> catList = null;
        try {
            catList = shareService.getShaCat();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return catList;
    }

    /**
     * 물건 등록 페이지 이동 및 카테고리 목록 조회
     *
     * @param model the model
     * @return the string
     * @since 2024 -07-22
     */
    @GetMapping("/register")
    public String register(Model model) {

        try {
            List<ShaCatDTO> list = shareService.getShaCat();
            model.addAttribute("catList", list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "share/registerForm";
    }

    /**
     * 물건 상세 조회 페이지 이동 및 상세 조회
     *
     * @param itemDTO the item dto
     * @param model   the model
     * @return the string
     * @since 2024 -07-23
     */
    @GetMapping(value = {"/rent/detail", "/give/detail"})
    public String itemDetail(ShaItemDTO itemDTO, Model model) {

        try {
            ShaItemDTO item = shareService.getItemDetail(itemDTO);
            model.addAttribute("item", item);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return "share/itemDetail";
    }

    /**
     * 물건 등록 및 첨부파일 저장 기능
     *
     * @param model    the model
     * @param itemDTO  the item dto
     * @param c        the principal
     * @param fileList the file list
     * @return the string
     * @since 2024 -07-22
     */
    @PostMapping("/register")
    public String insertItem(Model model, ShaItemDTO itemDTO, @AuthenticationPrincipal CustomUserDetails c, @RequestPart(name = "imgs") List<MultipartFile> fileList) {

        fileList = fileList.stream().filter((x) -> x.isEmpty() == false).collect(Collectors.toList());

        try {

            List<AttachmentDTO> imgList = new ArrayList<>();
            String imgPath = "C:\\Users\\user\\uploadFiles";

            if (!fileList.isEmpty()) {
                File mkdir = new File(imgPath);
                if (!mkdir.exists()) {
                    mkdir.mkdirs();
                }
                for (MultipartFile f : fileList) {

                    String originalFileName = f.getOriginalFilename();
                    String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                    AttachmentDTO img = new AttachmentDTO();
                    img.setOriginalName(originalFileName);
                    img.setSavedName(savedName);
                    img.setTypeCode(itemDTO.getItemGroup());

                    imgList.add(img);
                    f.transferTo(new File(imgPath + "\\" + savedName));

                }

            }

            itemDTO.setLocCode(c.getMemberDTO().getMemAddressDTO().getLocationCode());
            itemDTO.setOwnerNo(c.getMemberDTO().getMemberNo());
            int result = shareService.insertItem(itemDTO, imgList);

            if (result < 1) {
                for (AttachmentDTO img : imgList) {
                    new File(imgPath + "\\" + img.getSavedName()).delete();
                }
                throw new Exception();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "common/errorPage";
        }

        return itemDTO.getItemGroup().equals("rent") ? "redirect:/share/rent" : "redirect:/share/give";
    }


    /**
     * 물건 검색
     *
     * @param criteriaDTO the criteria dto
     * @return the list
     * @since 2024 -07-24
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchItems(ShaCriteriaDTO criteriaDTO, @AuthenticationPrincipal CustomUserDetails c) {

        Map<String, Object> result = new HashMap<>();
        ShaPageDTO pageInfo = new ShaPageDTO();
        List<ShaItemDTO> itemList = null;

        System.out.println("여기!!!!!" + criteriaDTO);

        try {
            criteriaDTO.setPageRange(criteriaDTO.getPage());
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }
            long locCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();
            if (locCode == 0) {
                throw new Exception("지역을 먼저 등록해주세요.");
            }
            criteriaDTO.setLocCode(locCode);
            itemList = shareService.searchItems(criteriaDTO);
            int totalCounts = shareService.getItemCounts(criteriaDTO);
            pageInfo.setCurrentPage(criteriaDTO.getPage());
            pageInfo.setPageInfo(totalCounts);
            System.out.println(itemList);
            result.put("itemList", itemList);
            result.put("pageInfo", pageInfo);

            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            result.put("msg", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(result);
        }
    }
}
