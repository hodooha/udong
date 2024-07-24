package com.multi.udong.login.controller;

import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.member.service.NTSAPI;
import com.multi.udong.member.service.NaverOcr;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The type Member controller.
 *
 * @author 김재식
 * @since 2024 -07-23
 */
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private final NTSAPI ntsapi;


    /**
     * Login string.
     *
     * @return the string
     * @since 2024 -07-23
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "message", required = false) String message,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", message);
        }
        return "member/login";
    }

    /**
     * Signup string.
     *
     * @param model the model
     * @return the string
     * @since 2024 -07-23
     */
    @GetMapping("/signup")
    public String signup(Model model) {
        System.out.println("회원가입 시작");
        model.addAttribute("memberDTO", new MemberDTO());
        return "member/signup";
    }

    @Value("${nts.api.service-key}")
    private String serviceKey;

    /**
     * Signup string.
     *
     * @param m       the m
     * @param request the request
     * @param file    the file
     * @return the string
     * @since 2024 -07-23
     */
    @PostMapping("/signup")
    public String signup(MemberDTO m, HttpServletRequest request, @RequestParam("file") MultipartFile file) {

        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        String randomNickname = String.format("member-%05d", randomNumber);
        m.setNickname(randomNickname);

        System.out.println("m" + m);

        if (file != null && !file.isEmpty()) {
            // 파일명 랜덤 저장
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 저장 경로 설정
            String savePath = "C:\\workspace\\local\\udong\\src\\main\\resources\\static\\uploadFiles\\";
            System.out.println(savePath);

            // 저장될 폴더 설정
            File mkdir = new File(savePath);
            if (!mkdir.exists()) {
                mkdir.mkdirs();
            }

            // File 객체 생성
            File target = new File(savePath + savedName);
            System.out.println(target);

            // file 저장
            try {
                file.transferTo(target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // NaverOcr로 사업자등록증 추출요청
            NaverOcr ocr = new NaverOcr();
            String fileName = savePath + savedName;
            ArrayList<String> list = ocr.ocr(fileName);
            System.out.println("list:" + list);

            String b_no = list.get(0).replaceAll("-","");
            String p_nm = list.get(2);
            String start_dt = list.get(3).replaceAll(" ","").replaceAll("년","").replaceAll("월","").replaceAll("일","");

            // 추출한 정보를 국세청API로 검증요청
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, String>> businesses = new ArrayList<>();
            Map<String, String> business = new HashMap<>();

            business.put("b_no", b_no);
            business.put("p_nm", p_nm);
            business.put("start_dt", start_dt);
            businesses.add(business);
            requestBody.put("businesses", businesses);

            // 검증결과에서 "data": [{"valid": "01"}] 값 추출
            Map<String, Object> result = ntsapi.validateBusinessRegistration(serviceKey, requestBody);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) result.get("data");
            Map<String, Object> data = dataList.get(0);
            String valid = (String) data.get("valid");
            System.out.println("valid:" + valid);

            // valid값이 01 이면 파일과 MemBusDTO를 저장
            if (valid.equals("01")) {
                try {
                    MemBusDTO memBusDTO = new MemBusDTO();
                    memBusDTO.setBusinessNumber(b_no);
                    memBusDTO.setRepresentativeName(p_nm);
                    memBusDTO.setOpeningDate(start_dt);
                    memberService.signupSeller(m, memBusDTO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return "redirect:/login";
            } else {
                new File(fileName).delete();
                return "redirect:/signup";
            }
        }

        try {
            memberService.signup(m);
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/signup";
        }
    }

    /**
     * Is id duplicate response entity.
     *
     * @param request the request
     * @return the response entity
     * @since 2024 -07-23
     */
    @PostMapping("/isIdDuplicate")
    public ResponseEntity<String> isIdDuplicate(@RequestBody MemberDTO request) {
        boolean isDuplicate = memberService.isIdDuplicate(request.getMemberId());
        if (isDuplicate) {
            return ResponseEntity.ok("duplicate");
        } else {
            return ResponseEntity.ok("available");
        }
    }
}

