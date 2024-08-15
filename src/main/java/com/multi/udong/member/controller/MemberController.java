package com.multi.udong.member.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.controller.KakaoLoginController;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

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
     * @param c     the c
     * @param model the model
     * @since 2024 -07-24
     */
    @GetMapping("/dashBoard")
    public void dashBoard(@AuthenticationPrincipal CustomUserDetails c, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();

        Map<String, Object> map = memberService.selectAllDashBoard(memberNo);

        System.out.println("##### map : " + map);

        model.addAttribute("newsData", map.get("newsData"));
        model.addAttribute("lendData", map.get("lendData"));
        model.addAttribute("rentData", map.get("rentData"));
        model.addAttribute("giveData", map.get("giveData"));
        model.addAttribute("clubData", map.get("clubData"));
        model.addAttribute("scheduleData", map.get("scheduleData"));
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

        }
        
        model.addAttribute("currentFullAddress", currentFullAddress);
        model.addAttribute("kakaoApiKey", kakaoLoginController.getKakaoApiKey());
    }

    /**
     * Act.
     *
     * @param c              the c
     * @param table          the table
     * @param page           the page
     * @param searchCategory the search category
     * @param searchWord     the search word
     * @param model          the model
     * @since 2024 -07-24
     */
    @GetMapping("/act")
    public void act(@AuthenticationPrincipal CustomUserDetails c,
                    @RequestParam("table") String table,
                    @RequestParam(value = "page", defaultValue = "1") int page,
                    @RequestParam(value = "searchCategory", required = false) String searchCategory,
                    @RequestParam(value = "searchWord", required = false) String searchWord,
                    Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPage(page);
        pageDTO.setMemberNo(memberNo);
        pageDTO.setStartEnd(page);

        pageDTO.setSearchCategory(searchCategory);
        pageDTO.setSearchWord(searchWord);

        int count;
        int pages = 1;

        List<List<String>> data = memberService.selectAllAct(table, pageDTO);

        if (!data.isEmpty()) {
            count = Integer.parseInt(data.get(0).get(data.get(0).size() - 1));
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;

            for (List<String> list : data) {
                list.remove(list.size() - 1);
            }
        }

        List<String> headers = getHeaders(table);
        List<String> searchCategories = getSearchCategories(table);
        int searchCategoryIndex = headers.indexOf(searchCategory);

        model.addAttribute("tableHeaders", headers);
        model.addAttribute("tableData", data);

        model.addAttribute("page", pageDTO.getPage());
        model.addAttribute("table", table);
        model.addAttribute("pages", pages);

        model.addAttribute("searchCategories", searchCategories);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchCategoryIndex", searchCategoryIndex);
        model.addAttribute("searchWord", searchWord);
    }

    private List<String> getHeaders(String table) {
        return switch (table) {
            case "newsBoard" -> Arrays.asList("주제", "동네", "제목", "작성일", "조회수");
            case "newsLike" -> Arrays.asList("주제", "동네", "제목", "작성일", "작성자", "조회수");
            case "newsReply" -> Arrays.asList("주제", "동네", "제목", "내용", "작성일");
            case "club" -> Arrays.asList("주제", "동네", "모임명", "모임장", "생성일");
            case "clubLog" -> Arrays.asList("주제", "동네", "모임명", "제목", "작성일", "조회수");
            case "clubSchedule" -> Arrays.asList("주제", "동네", "모임명", "제목", "작성자", "일시");
            case "shareLike" -> Arrays.asList("카테고리", "동네", "물품명" , "상태");
            case "saleBoard" -> Arrays.asList("동네", "물품명", "정상가", "할인가", "작성일", "시작시간", "종료시간");
            default -> new ArrayList<>();
        };
    }

    private List<String> getSearchCategories(String table) {
        return switch (table) {
            case "newsBoard" -> Arrays.asList("주제", "동네", "제목", "내용");
            case "newsLike" -> Arrays.asList("주제", "동네", "제목", "내용", "작성자");
            case "newsReply" -> Arrays.asList("주제", "동네", "제목", "댓글내용");
            case "club" -> Arrays.asList("주제", "동네", "모임명", "모임장");
            case "clubLog" -> Arrays.asList("주제", "동네", "모임명", "제목");
            case "clubSchedule" -> Arrays.asList("주제", "동네", "모임명", "제목", "작성자");
            case "shareLike" -> Arrays.asList("카테고리", "동네", "물품명");
            case "saleBoard" -> Arrays.asList("동네", "물품명");
            default -> new ArrayList<>();
        };
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
     * @param c                  the c
     * @param memAddressDTO      the mem address dto
     * @param redirectAttributes the redirect attributes
     * @param model              the model
     * @return the string
     * @since 2024 -07-25
     */
    @PostMapping("/insertAddress")
    public String insertAddress(@AuthenticationPrincipal CustomUserDetails c,
                                MemAddressDTO memAddressDTO,
                                RedirectAttributes redirectAttributes,
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
                model.addAttribute("msg", e.getMessage());
                return "common/errorPage";
            }
            
            // 등록 후 사용자 세션 최신화
            memberService.updateMemberSession();
            redirectAttributes.addFlashAttribute("alert", "주소 등록이 완료되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "success");

        } else { // 등록된 주소가 있다면

            // 주소 수정 진행
            try {
                memberService.updateAddress(memAddressDTO);
                
            } catch (Exception e) {
                model.addAttribute("msg", e.getMessage());
                return "common/errorPage";
            }

            // 수정 후 사용자 세션 최신화
            memberService.updateMemberSession();
            redirectAttributes.addFlashAttribute("alert", "주소 수정이 완료되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "success");

        }

        return "redirect:/member/address";
    }

    /**
     * 입력한 정보로 현재 사용자의 회원정보를 수정함
     *
     * @param c                  the c
     * @param file               the file
     * @param currentPw          the current pw
     * @param memberDTO          the member dto
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-26
     */
    @PostMapping("/updateProfile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails c,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam(value = "current-password", required = false) String currentPw,
                                MemberDTO memberDTO,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        // memberDTO에 현재 사용자의 회원번호를 입력
        int memberNo = c.getMemberDTO().getMemberNo();
        memberDTO.setMemberNo(memberNo);

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
            if (currentPw != null && memberDTO.getMemberPw() != null) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                // '입력한 현재 비밀번호' 와 '현재 사용자의 비밀번호' 가 일치하지 않으면
                if (!bCryptPasswordEncoder.matches(currentPw, c.getMemberDTO().getMemberPw())) {
                    redirectAttributes.addFlashAttribute("alert", "현재 비밀번호가 일치하지 않습니다.");
                    redirectAttributes.addFlashAttribute("alertType", "error");
                    return "redirect:/member/memInfo";
                }
            }

            // 프로필 수정 진행
            memberService.updateProfile(memberDTO, null);
        }

        // 수정 후 사용자 세션 최신화
        memberService.updateMemberSession();
        redirectAttributes.addFlashAttribute("alert", "회원정보 수정이 완료되었습니다.");
        redirectAttributes.addFlashAttribute("alertType", "success");
        return "redirect:/member/memInfo";
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
     * Delete member string.
     *
     * @param c                  the c
     * @param request            the request
     * @param response           the response
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-30
     */
    @PostMapping("/delete")
    public String deleteMember(@AuthenticationPrincipal CustomUserDetails c,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();

        String result = memberService.deleteMember(memberNo);

        switch (result) {
            case "isRenting":
                model.addAttribute("msg", "물품을 대여 중인 상태에서는 탈퇴할 수 없습니다. 물품을 반납해주세요.");
                return "common/errorPage";

            case "isGiving":
                model.addAttribute("msg", "물품을 나눔 중인 상태에서는 탈퇴할 수 없습니다. 나눔 중인 물품을 삭제해주세요.");
                return "common/errorPage";

            case "isMaster":
                model.addAttribute("msg", "모임장인 상태에서는 탈퇴할 수 없습니다. 모임장 권한을 양보해주세요.");
                return "common/errorPage";

            case "able":
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    new SecurityContextLogoutHandler().logout(request, response, auth);
                }

                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                redirectAttributes.addFlashAttribute("alert", "회원 탈퇴가 완료되었습니다. 우동행을 이용해 주셔서 감사합니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");
                return "redirect:/";

            default:
                model.addAttribute("msg", "회원 탈퇴에 실패하였습니다.");
                return "common/errorPage";
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
        String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
        String savePath = path + File.separator; // 운영 체제에 맞는 구분자 추가

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
