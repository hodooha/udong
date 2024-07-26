package com.multi.udong.member.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.controller.KakaoLoginController;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * The type Member controller.
 *
 * @author 김재식
 * @since 2024 -07-24
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final KakaoLoginController kakaoLoginController;

    /**
     * Dash board.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/dashBoard")
    public void dashBoard() {
    }

    /**
     * Mem info.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/memInfo")
    public void memInfo() {
    }

    /**
     * Address.
     *
     * @param c     the c
     * @param model the model
     * @since 2024 -07-24
     */
    @GetMapping("/address")
    public void address(@AuthenticationPrincipal CustomUserDetails c, Model model) {
        if (c != null) {
            MemAddressDTO addressDTO = c.getMemberDTO().getMemAddressDTO();
            String currentFullAddress = null;
            if (addressDTO.getLocationCode() != null) {
                currentFullAddress = addressDTO.getSiDoName() + " " +
                        addressDTO.getSiGunGuName() + " " +
                        addressDTO.getEupMyeonDongName() + " " +
                        (addressDTO.getDetailAddress() != null ? addressDTO.getDetailAddress() : "");
            } else {
                currentFullAddress = "등록된 주소가 없습니다. 주소 검색을 통해 주소 등록을 진행해주세요.";
            }
            model.addAttribute("currentFullAddress", currentFullAddress);
        }
        model.addAttribute("kakaoApiKey", kakaoLoginController.getKakaoApiKey());
    }

    /**
     * Act.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/act")
    public void act() {
    }

    /**
     * Message.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/message")
    public void message() {
    }

    /**
     * Noti set.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/notiSet")
    public void notiSet() {
    }

    /**
     * Mem del.
     *
     * @since 2024 -07-24
     */
    @GetMapping("/memDel")
    public void memDel() {
    }

    /**
     * Insert address string.
     *
     * @param c             the c
     * @param memAddressDTO the mem address dto
     * @param model         the model
     * @return the string
     * @since 2024 -07-25
     */
    @PostMapping("/insertAddress")
    public String insertAddress(@AuthenticationPrincipal CustomUserDetails c,
                                MemAddressDTO memAddressDTO,
                                Model model) {
        Long currentLocationCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();

        if (currentLocationCode == null) {
            memAddressDTO.setMemberNo(c.getMemberDTO().getMemberNo());
            try {
                memberService.insertAddress(memAddressDTO);
            } catch (Exception e) {
                model.addAttribute("msg", "주소 등록에 실패하였습니다.");
                throw new RuntimeException(e);
            }
            memberService.updateMemberSession();
        } else {
            updateAddress(c, memAddressDTO);
            model.addAttribute("msg", "주소 수정이 완료되었습니다.");
        }

        model.addAttribute("msg", "주소 등록이 완료되었습니다.");
        return "member/address";
    }

    /**
     * Update address string.
     *
     * @param c             the c
     * @param memAddressDTO the mem address dto
     * @since 2024 -07-25
     */
    public void updateAddress(@AuthenticationPrincipal CustomUserDetails c, MemAddressDTO memAddressDTO) {
        memAddressDTO.setMemberNo(c.getMemberDTO().getMemberNo());
        try {
            memberService.updateAddress(memAddressDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        memberService.updateMemberSession();
    }

    /**
     * Update profile.
     *
     * @param c         the c
     * @param file      the file
     * @param memberDTO the member dto
     * @since 2024 -07-26
     */
    public void updateProfile(@AuthenticationPrincipal CustomUserDetails c,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              MemberDTO memberDTO) {
        memberDTO.setMemberNo(c.getMemberDTO().getMemberNo());
        if (file != null) {
            AttachmentDTO attachmentDTO = settingFile(file);

            memberService.updateProfile(memberDTO, attachmentDTO);

        }

    }

    /**
     * Setting file.
     *
     * @param file the file
     * @return the attachment dto
     * @since 2024 -07-26
     */
    public AttachmentDTO settingFile(MultipartFile file) {
        // 파일명 랜덤 저장
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 저장 경로 설정
        String savePath = "C:\\workspace\\local\\udong\\src\\main\\resources\\static\\uploadFiles\\";

        // 저장될 폴더 설정
        File mkdir = new File(savePath);
        if (!mkdir.exists()) {
            mkdir.mkdirs();
        }

        // File 객체 생성
        File target = new File(savePath + savedName);

        // file 저장
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 반환 정보 저장
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setSavedName(savedName);
        attachmentDTO.setOriginalName(originalName);

        return attachmentDTO;
    }
}
