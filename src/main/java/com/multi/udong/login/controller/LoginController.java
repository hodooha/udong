package com.multi.udong.login.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.openFeign.NTSAPI;
import com.multi.udong.login.service.CustomUserDetailsService;
import com.multi.udong.member.controller.MemberController;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.member.service.NaverOcr;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Paths;
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
    private final CustomUserDetailsService customUserDetailsService;
    private final NaverOcr naverOcr;

    /**
     * 로그인 메소드, 로그인 실패시 에러메세지를 받아옴
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
            model.addAttribute("alert", message);
            model.addAttribute("alertType", "error");
        }
        return "member/login";
    }

    /**
     * Select role string.
     *
     * @return the string
     * @since 2024 -08-09
     */
    @GetMapping("/selectRole")
    public String selectRole() {
        return "member/selectRole";
    }

    /**
     * Signup string.
     *
     * @param model the model
     * @return the string
     * @since 2024 -07-23
     */
    @GetMapping("/signup/member")
    public String signupMember(Model model) {
        return "member/signupMember";
    }

    /**
     * Signup seller string.
     *
     * @param model the model
     * @return the string
     * @since 2024 -08-09
     */
    @GetMapping("/signup/seller")
    public String signupSeller(Model model) {
        return "member/signupSeller";
    }

    @Value("${nts.api.service-key}")
    private String serviceKey;

    /**
     * 입력한 사용자 정보로 회원가입
     *
     * @param memberDTO          the memberDTO
     * @param memBus             the mem bus
     * @param request            the request
     * @param file               the file
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-23
     */
    @PostMapping("/signup")
    public String signup(MemberDTO memberDTO,
                         MemBusDTO memBus,
                         HttpServletRequest request,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        // 초기가입시 닉네임을 랜덤하게 저장
        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        String randomNickname = String.format("member-%05d", randomNumber);
        memberDTO.setNickname(randomNickname);

        // 사업자등록증 파일이 있다면
        if (file != null) {
            
            // 파일 저장 메소드
            AttachmentDTO attachmentDTO = memberController.settingFile(file);
            attachmentDTO.setTypeCode("BRG");

            String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
            String savePath = path + File.separator; // 운영 체제에 맞는 구분자 추가
            String fileName = savePath + attachmentDTO.getSavedName();

            // NaverOcr로 사업자등록증 추출요청
            ArrayList<String> list = naverOcr.ocr(fileName);

            // 추출된 결과가 있다면
            if (!list.isEmpty()) {

                // 국세청 api로 유효코드 반환
                String valid = ntsAPIValid(list);

                // valid값이 01이면 회원가입 처리 + 사업자등록증 정보와 첨부파일도 같이 등록
                if (valid.equals("01")) {
                    
                    try {
                        String b_no = list.get(0).trim().replaceAll("-", "");
                        String p_nm = list.get(1).replaceAll(",", "").trim();
                        String start_dt = list.get(2).trim()
                                .replaceAll(" ", "")
                                .replaceAll("년", "")
                                .replaceAll("월", "")
                                .replaceAll("일", "");

                        MemBusDTO memBusDTO = new MemBusDTO();
                        memBusDTO.setBusinessNumber(b_no);
                        memBusDTO.setRepresentativeName(p_nm);
                        memBusDTO.setOpeningDate(start_dt);
                        memberService.signupSeller(memberDTO, memBusDTO, attachmentDTO);
                        
                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("alert", "중복된 사업자등록증입니다.<br>등록증 당 한 계정만 가입할 수 있습니다.");
                        redirectAttributes.addFlashAttribute("alertType", "error");
                        return "redirect:/signup/seller";
                    }

                    authenticateUserAndSetSession(memberDTO, request);
                    redirectAttributes.addFlashAttribute("alert", "회원가입이 완료되었습니다!<br>승인결과는 사이트 내 알림으로 전송됩니다.");
                    redirectAttributes.addFlashAttribute("alertType", "signup");
                    return "redirect:/";
                    
                } else { // valid=02 일 경우 입력한 정보로 검증 시도

                    if (memBus.getBusinessNumber().isEmpty()) {
                        redirectAttributes.addFlashAttribute("alert", "유효하지 않은 사업자등록증입니다.<br>직접 정보를 입력해 다시 시도해보세요.");
                        redirectAttributes.addFlashAttribute("alertType", "error");
                        return "redirect:/signup/seller";
                    }

                    String b_no = memBus.getBusinessNumber();
                    String p_nm = memBus.getRepresentativeName();
                    String start_dt = memBus.getOpeningDate();
                    ArrayList<String> listInput = new ArrayList<>();
                    listInput.add(b_no);
                    listInput.add(p_nm);
                    listInput.add(start_dt);
                    String validInput = ntsAPIValid(listInput);

                    // validInput 값이 01이면 회원가입 처리 + 사업자등록증 정보와 첨부파일도 같이 등록
                    if (validInput.equals("01")) {

                        try {
                            MemBusDTO memBusDTO = new MemBusDTO();
                            memBusDTO.setBusinessNumber(b_no);
                            memBusDTO.setRepresentativeName(p_nm);
                            memBusDTO.setOpeningDate(start_dt);
                            memberService.signupSeller(memberDTO, memBusDTO, attachmentDTO);

                        } catch (Exception e) {
                            redirectAttributes.addFlashAttribute("alert", "중복된 사업자등록증입니다.<br>한 사업자등록증으로 한 계정만 가입할 수 있습니다.");
                            redirectAttributes.addFlashAttribute("alertType", "error");
                            return "redirect:/signup/seller";
                        }

                        authenticateUserAndSetSession(memberDTO, request);
                        redirectAttributes.addFlashAttribute("alert", "회원가입이 완료되었습니다!<br>승인결과는 사이트 내 알림으로 전송됩니다.");
                        redirectAttributes.addFlashAttribute("alertType", "signup");
                        return "redirect:/";

                    } else { // valid=02 일 경우

                        redirectAttributes.addFlashAttribute("alert", "유효하지 않은 사업자등록증입니다.");
                        redirectAttributes.addFlashAttribute("alertType", "error");
                        return "redirect:/signup/seller";
                    }
                }
                
            } else { // OCR로 추출한 결과가 없다면 입력한 정보로 검증 시도

                if (memBus.getBusinessNumber().isEmpty()) {
                    redirectAttributes.addFlashAttribute("alert", "유효하지 않은 이미지입니다.<br>직접 정보를 입력해 다시 시도해보세요.");
                    redirectAttributes.addFlashAttribute("alertType", "error");
                    return "redirect:/signup/seller";
                }

                String b_no = memBus.getBusinessNumber();
                String p_nm = memBus.getRepresentativeName();
                String start_dt = memBus.getOpeningDate();
                ArrayList<String> listInput = new ArrayList<>();
                listInput.add(b_no);
                listInput.add(p_nm);
                listInput.add(start_dt);
                String validInput = ntsAPIValid(listInput);

                // validInput 값이 01이면 회원가입 처리 + 사업자등록증 정보와 첨부파일도 같이 등록
                if (validInput.equals("01")) {

                    try {
                        MemBusDTO memBusDTO = new MemBusDTO();
                        memBusDTO.setBusinessNumber(b_no);
                        memBusDTO.setRepresentativeName(p_nm);
                        memBusDTO.setOpeningDate(start_dt);
                        memberService.signupSeller(memberDTO, memBusDTO, attachmentDTO);

                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("alert", "중복된 사업자등록증입니다.<br>한 사업자등록증으로 한 계정만 가입할 수 있습니다.");
                        redirectAttributes.addFlashAttribute("alertType", "error");
                        return "redirect:/signup/seller";
                    }

                    authenticateUserAndSetSession(memberDTO, request);
                    redirectAttributes.addFlashAttribute("alert", "회원가입이 완료되었습니다!<br>승인결과는 사이트 내 알림으로 전송됩니다.");
                    redirectAttributes.addFlashAttribute("alertType", "signup");
                    return "redirect:/";

                } else { // valid=02 일 경우

                    redirectAttributes.addFlashAttribute("alert", "유효하지 않은 이미지입니다.<br>다시 촬영해주세요.");
                    redirectAttributes.addFlashAttribute("alertType", "error");
                    return "redirect:/signup/seller";
                }
            }
        }

        try {
            memberService.signup(memberDTO);
            authenticateUserAndSetSession(memberDTO, request);
            redirectAttributes.addFlashAttribute("alert","회원가입이 완료되었습니다!");
            redirectAttributes.addFlashAttribute("alertType", "signup");
            return "redirect:/";
            
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            return "common/errorPage";
        }
    }

    /**
     * 국세청 API 사업자등록증 진위확인
     * @param list
     * @return
     */
    private String ntsAPIValid (ArrayList<String> list) {

        // 추출한 결과 전처리
        String b_no = list.get(0).trim().replaceAll("-", "");
        String p_nm = list.get(1).trim()
                .replaceAll(",", "").trim();
        String start_dt = list.get(2).trim()
                .replaceAll(" ", "")
                .replaceAll("년", "")
                .replaceAll("월", "")
                .replaceAll("일", "");

        // 전처리된 추출 결과를 국세청API로 검증요청
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, String>> businesses = new ArrayList<>();
        Map<String, String> business = new HashMap<>();

        business.put("b_no", b_no);
        business.put("p_nm", p_nm);
        business.put("start_dt", start_dt);
        businesses.add(business);
        requestBody.put("businesses", businesses);

        Map<String, Object> result = ntsapi.validateBusinessRegistration(serviceKey, requestBody);

        System.out.println("##### result : " + result);

        // 검증결과에서 "data": [{"valid": "01"}] 값 추출, 01 = 유효, 02 = 유효하지 않음
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) result.get("data");
        Map<String, Object> data = dataList.get(0);
        String valid = (String) data.get("valid");
        System.out.println("사업자등록증 유효코드 - valid: " + valid);

        return valid;
    }

    /**
     * ID 중복확인 메소드
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

    /**
     * Handle error string.
     *
     * @param alert              the alert
     * @param alertType          the alert type
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -08-22
     */
    @GetMapping("/error")
    public String handleError(@RequestParam("alert") String alert,
                              @RequestParam("alertType") String alertType,
                              RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("alert", alert);
        redirectAttributes.addFlashAttribute("alertType", alertType);
        return "redirect:/";
    }

    /**
     * 현재 사용자 세션 업데이트
     *
     * @param memberDTO the member dto
     * @param request   the request
     * @since 2024 -08-02
     */
    public void authenticateUserAndSetSession(MemberDTO memberDTO, HttpServletRequest request) {
        String username = memberDTO.getMemberId();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // https://devjun.tistory.com/92

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        System.out.println("token : " + token);

        // SecurityContext 에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(token);

        // 세션에 SecurityContext 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }
}
