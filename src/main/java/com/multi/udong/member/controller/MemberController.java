package com.multi.udong.member.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.controller.KakaoLoginController;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
     * 회원정보 관리 페이지 메소드, 현재 사용자 정보를 보냄
     *
     * @param c     the c
     * @param model the model
     * @since 2024 -07-24
     */
    @GetMapping("/memInfo")
    public void memInfo(@AuthenticationPrincipal CustomUserDetails c, Model model) {

        if (c != null) {

            model.addAttribute("nickname", c.getMemberDTO().getNickname());
            model.addAttribute("email", c.getMemberDTO().getEmail());
            model.addAttribute("phone", c.getMemberDTO().getPhone());

        }
    }

    /**
     * 주소 관리 페이지 메소드,현재 사용자의 주소 정보와 카카오 API키를 보냄
     *
     * @param c     the c
     * @param model the model
     * @since 2024 -07-24
     */
    @GetMapping("/address")
    public void address(@AuthenticationPrincipal CustomUserDetails c, Model model) {
        
        MemAddressDTO addressDTO = c.getMemberDTO().getMemAddressDTO();
        String currentFullAddress = null;

        // 등록된 주소가 있다면
        if (addressDTO.getLocationCode() != null) {

            // 전체 주소정보 전처리
            currentFullAddress = addressDTO.getSiDoName() + " " +
                    addressDTO.getSiGunGuName() + " " +
                    addressDTO.getEupMyeonDongName() + " " +
                    (addressDTO.getDetailAddress() != null ? addressDTO.getDetailAddress() : "");

        } else { // 등록된 주소가 없다면
            currentFullAddress = "등록된 주소가 없습니다. 주소 검색을 통해 주소 등록을 진행해주세요.";
        }
        
        model.addAttribute("currentFullAddress", currentFullAddress);
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
     * 입력한 주소를 등록하거나 수정함
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
        
        // 등록된 주소의 주소코드를 받아옴
        Long currentLocationCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();

        // 입력한 주소 정보에 현재 사용자의 회원번호를 입력함
        memAddressDTO.setMemberNo(c.getMemberDTO().getMemberNo());

        // 등록된 주소가 없다면
        if (currentLocationCode == null) {
            
            // 주소 등록 진행
            try {
                memberService.insertAddress(memAddressDTO);
                
            } catch (Exception e) {
                model.addAttribute("msg", "주소 등록에 실패하였습니다.");
                throw new RuntimeException(e);
            }
            
            // 등록 후 사용자 세션 최신화
            memberService.updateMemberSession();
            model.addAttribute("msg", "주소 등록이 완료되었습니다.");

        } else { // 등록된 주소가 있다면

            // 주소 수정 진행
            try {
                memberService.updateAddress(memAddressDTO);
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 수정 후 사용자 세션 최신화
            memberService.updateMemberSession();
            model.addAttribute("msg", "주소 수정이 완료되었습니다.");

        }

        return "member/dashBoard";
    }
    
    /**
     * 입력한 정보로 현재 사용자의 회원정보를 수정함
     *
     * @param c         the c
     * @param file      the file
     * @param memberDTO the member dto
     * @param model     the model
     * @return the string
     * @since 2024 -07-26
     */
    @PostMapping("/updateProfile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails c,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              MemberDTO memberDTO,
                              Model model) {

        // memberDTO에 현재 사용자의 회원번호를 입력
        memberDTO.setMemberNo(c.getMemberDTO().getMemberNo());

        // 프로필 사진를 수정할 때
        if (file != null && !file.isEmpty()) {

            // 첨부파일 저장 및 TargetNo, TypeCode 입력
            AttachmentDTO attachmentDTO = settingFile(file);
            attachmentDTO.setTargetNo(c.getMemberDTO().getMemberNo());
            attachmentDTO.setTypeCode("MEM");

            // 프로필 수정 진행
            memberService.updateProfile(memberDTO, attachmentDTO);

        } else { // 프로필 사진을 수정하지 않을 때

            // 비밀번호를 수정할 때
            if (memberDTO.getMemberPw() != null) {

                // '현재 사용자의 비밀번호'와 입력한 '현재 비밀번호'가 일치하지 않으면
                if (memberDTO.getMemberPw() != c.getMemberDTO().getMemberPw()) {
                    model.addAttribute("msg", "현재 비밀번호가 일치하지 않습니다.");
                    return "member/memInfo";
                }

            }

            // 프로필 수정 진행
            memberService.updateProfile(memberDTO, null);
        }

        // 수정 후 사용자 세션 최신화
        memberService.updateMemberSession();
        model.addAttribute("msg", "회원정보 수정이 완료되었습니다.");
        return "member/dashBoard";
    }

    /**
     * 닉네임 중복확인 메소드
     *
     * @param request the request
     * @return the response entity
     * @since 2024 -07-27
     */
    @PostMapping("/isNicknameDuplicate")
    public ResponseEntity<String> isNicknameDuplicate(@RequestBody MemberDTO request) {

        boolean isDuplicate = memberService.isNicknameDuplicate(request.getNickname());

        if (isDuplicate) {
            return ResponseEntity.ok("duplicate");

        } else {
            return ResponseEntity.ok("available");
        }
    }

    /**
     * 파일 저장 처리
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
