package com.multi.udong.share.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.security.CustomUserDetails;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaCriteriaDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
     * 대여 메인페이지 이동 및 목록 조회
     *
     * @param c     the c
     * @param model the model
     * @return the string
     * @since 2024 -07-22
     */
    @GetMapping("/rent")
    public String rentMain(@AuthenticationPrincipal CustomUserDetails c, Model model) {
        int locCode = 1111010100; // 추후 변경 필요!

        try {
            List<ShaItemDTO> itemList = shareService.rentItemList(locCode);
            List<ShaCatDTO> catList = shareService.getShaCat();
            System.out.println(itemList);
            model.addAttribute("itemList", itemList);
            model.addAttribute("catList", catList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "share/rentMain";
    }


    /**
     * 나눔 메인페이지 이동 및 목록 조회
     *
     * @param c     the c
     * @param model the model
     * @return the string
     * @since 2024 -07-22
     */
    @GetMapping("/give")
    public String giveMain(@AuthenticationPrincipal CustomUserDetails c, Model model) {
        int locCode = 1111010100; // 추후 변경 필요!

        try {
            List<ShaItemDTO> itemList = shareService.giveItemList(locCode);
            List<ShaCatDTO> catList = shareService.getShaCat();
            System.out.println(itemList);
            model.addAttribute("itemList", itemList);
            model.addAttribute("catList", catList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "share/giveMain";
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
    @GetMapping(value= {"/rent/detail", "/give/detail"})
    public String itemDetail(ShaItemDTO itemDTO, Model model){

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
     * @param model     the model
     * @param itemDTO   the item dto
     * @param principal the principal
     * @param fileList  the file list
     * @return the string
     * @since 2024 -07-22
     */
    @PostMapping("/register")
    public String insertItem(Model model, ShaItemDTO itemDTO, Principal principal, @RequestPart(name = "imgs") List<MultipartFile> fileList) {

        fileList = fileList.stream().filter((x) -> x.isEmpty() == false).collect(Collectors.toList());
        itemDTO.setLocCode(1111010100L); // 추후 수정 필요!
        itemDTO.setOwnerNo(1); // 추후 수정필요!

        try {

            List<AttachmentDTO> imgList = new ArrayList<>();
            String imgPath = "C:\\workspace\\multi\\final\\udong\\src\\main\\resources\\static\\uploadFiles";

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

            int result = shareService.insertItem(itemDTO, imgList);

            if (result < 1) {
                for (AttachmentDTO img : imgList) {
                    new File(imgPath + "\\" + img.getSavedName()).delete();
                }
                throw new Exception();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return "redirect:/share/rent";
    }


    /**
     * 물건 검색
     *
     * @param criteriaDTO the criteria dto
     * @return the list
     * @since 2024 -07-24
     */
    @PostMapping("/searchByCat")
    @ResponseBody
    public List<ShaItemDTO> searchItems(ShaCriteriaDTO criteriaDTO){

        System.out.println(criteriaDTO);
        List<ShaItemDTO> itemList = null;
        try {
            itemList = shareService.searchItems(criteriaDTO);
            System.out.println(itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return itemList;
    }

}
