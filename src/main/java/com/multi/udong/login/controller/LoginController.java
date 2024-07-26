package com.multi.udong.login.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.controller.MemberController;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private final MemberController memberController;

    /**
     * Login string.
     *
     * @param error   the error
     * @param message the message
     * @param model   the model
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
     * @param memberDTO the memberDTO
     * @param request   the request
     * @param file      the file
     * @param model     the model
     * @return the string
     * @since 2024 -07-23
     */
    @PostMapping("/signup")
    public String signup(MemberDTO memberDTO,
                         HttpServletRequest request,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         Model model) {

        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        String randomNickname = String.format("member-%05d", randomNumber);
        memberDTO.setNickname(randomNickname);

        System.out.println("memberDTO" + memberDTO);

        if (!file.isEmpty()) {
            AttachmentDTO attachmentDTO = memberController.settingFile(file);
            attachmentDTO.setTypeCode("BRG");

            String savePath = "C:\\workspace\\local\\udong\\src\\main\\resources\\static\\uploadFiles\\";
            String fileName = savePath + attachmentDTO.getSavedName();

            // NaverOcr로 사업자등록증 추출요청
            NaverOcr ocr = new NaverOcr();
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

            // valid값이 01 회원가입 처리 + 사업자등록증 정보와 첨부파일도 같이 등록
            if (valid.equals("01")) {
                try {
                    MemBusDTO memBusDTO = new MemBusDTO();
                    memBusDTO.setBusinessNumber(b_no);
                    memBusDTO.setRepresentativeName(p_nm);
                    memBusDTO.setOpeningDate(start_dt);
                    memberService.signupSeller(memberDTO, memBusDTO, attachmentDTO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return "redirect:/login";
            } else { // 유효하지 않으면 파일 삭제
                new File(fileName).delete();
                return "redirect:/signup";
            }
        }

        try {
            memberService.signup(memberDTO);
            model.addAttribute("msg","회원가입이 완료되었습니다.");
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

