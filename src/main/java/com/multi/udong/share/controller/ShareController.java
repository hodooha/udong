package com.multi.udong.share.controller;

import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/share")
@Controller
public class ShareController {

    private final ShareService shareService;

    @GetMapping("/rent")
    public String rentMain(){
        return "share/rentMain";
    }

    @GetMapping("/give")
    public String giveMain(){
        return "share/giveMain";
    }

    @GetMapping("/register")
    public String register(Model model){

        try {
            List<ShaCatDTO> list = shareService.getShaCat();
            model.addAttribute("catList", list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "share/registerForm";
    }

    @PostMapping("/insert")
    public String insertItem(ShaItemDTO itemDTO, Principal principal, @RequestPart MultipartFile multipartFile){

        System.out.println(itemDTO);
        itemDTO.setLocCode(1111010100);
        itemDTO.setOwnerNo(1);
        try {
            int result = shareService.insertItem(itemDTO);
            if(result<0){
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "share/rentMain";
    }

}
