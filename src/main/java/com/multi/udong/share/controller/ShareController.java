package com.multi.udong.share.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.security.CustomUserDetails;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/share")
@Controller
public class ShareController {

    private final ShareService shareService;

    @GetMapping("/rent")
    public String rentMain(@AuthenticationPrincipal CustomUserDetails c, Model model) {
        int locCode = 1111010100; // 추후 변경 필요!

        try {
            List<ShaItemDTO> itemList = shareService.rentItemList(locCode);
            System.out.println(itemList);
            model.addAttribute("itemList", itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "share/rentMain";
    }


    @GetMapping("/give")
    public String giveMain(@AuthenticationPrincipal CustomUserDetails c, Model model) {
        int locCode = 1111010100; // 추후 변경 필요!

        try {
            List<ShaItemDTO> itemList = shareService.giveItemList(locCode);
            System.out.println(itemList);
            model.addAttribute("itemList", itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "share/giveMain";
    }

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

    @PostMapping("/insert")
    public String insertItem(Model model, ShaItemDTO itemDTO, Principal principal, @RequestPart(name = "imgs") List<MultipartFile> fileList) {

        fileList = fileList.stream().filter((x) -> x.isEmpty() == false).collect(Collectors.toList());
        itemDTO.setLocCode(1111010100); // 추후 수정 필요!
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
                System.out.println("여기!" + imgList);

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

        return "share/rentMain";
    }

}
