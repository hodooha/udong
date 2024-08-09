package com.multi.udong.club.controller;

import com.multi.udong.club.model.dto.*;
import com.multi.udong.club.service.ClubService;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import com.multi.udong.login.service.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 모임 Controller
 *
 * @author 강성현
 * @since 2024 -07-23
 */
@Controller
@RequestMapping("/club")
public class ClubController {

    private final ClubService clubService;

    /**
     * 모임 controller 생성자
     *
     * @param clubService the club service
     */
    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }


    /**
     * 우동 모임 메인 페이지로 이동
     *
     * @param c         the c
     * @param filterDTO the filter dto
     * @param model     the model
     * @return the string
     * @since 2024 -07-23
     */
    @RequestMapping("/clubMain")
    public String clubMain(@AuthenticationPrincipal CustomUserDetails c, FilterDTO filterDTO, Model model) {

        // 받아온 page로 시작 및 시작 index를 설정
        filterDTO.setStartAndStartIndex(filterDTO.getPage());

        // 검색 조건 확인
        System.out.println("###### 검색하는 카테고리 코드: " + filterDTO.getCategoryCode());
        System.out.println("###### 검색하는 검색어: " + filterDTO.getSearchWord());

        // 로그인된 유저의 locationCode를 principal에서 받아와 filterDTO에 set
        long membersLocation = c.getMemberDTO().getMemAddressDTO().getLocationCode();
        filterDTO.setLocationCode(membersLocation);

        try {

            // service의 모임 리스트 select 메소드 호출
            List<ClubDTO> clubList = clubService.selectClubList(filterDTO);
            System.out.println("###### 가져온 모임 리스트: " + clubList);
            model.addAttribute("clubList", clubList);

            // 모임의 총 개수를 받아와 생길 page의 총 개수 계산
            int clubCount = clubService.selectClubCount(filterDTO);
            System.out.println("###### 모임 총 개수: " + clubCount);

            int pages = 0;
            if(clubCount != 0) {

                pages = clubCount / 5;

                if(clubCount % 5 != 0) {
                    pages += 1;
                }

            }
            System.out.println("###### 페이지 개수: " + pages);

            model.addAttribute("pages", pages);
            model.addAttribute("filter", filterDTO);

            return "club/clubMain";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "모임 리스트 조회 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    /**
     * 모임 생성폼으로 이동
     *
     * @since 2024 -07-23
     */
    @RequestMapping("clubInsertForm")
    public void clubInsertForm() {

    }


    /**
     * 모임 카테고리 리스트 조회
     *
     * @return the list
     * @since 2024 -07-27
     */
    @RequestMapping("/categoryList")
    @ResponseBody // 요청한 곳으로 json 데이터 전달
    public List<CategoryDTO> categoryList() {

        try {

            List<CategoryDTO> categoryList = clubService.selectCategoryList();

            System.out.println("###### 가져온 카테고리 리스트: " + categoryList);

            return categoryList;

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }


    /**
     * 모임 생성
     *
     * @param c           the c
     * @param categoryDTO the category dto
     * @param clubDTO     the club dto
     * @param img         the img
     * @param model       the model
     * @return the string
     * @since 2024 -07-23
     */
    @PostMapping("/insertClub")
    public String insertClub(@AuthenticationPrincipal CustomUserDetails c, CategoryDTO categoryDTO, ClubDTO clubDTO, @RequestParam("img") MultipartFile img, Model model) {

        // 로그인된 유저의 no를 principal에서 받아와 모임 생성자(master)로 clubDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        ClubMemberDTO clubMemberDTO = new ClubMemberDTO();
        clubMemberDTO.setMemberNo(memberNo);
        clubDTO.setMaster(clubMemberDTO);

        // 로그인된 유저의 locationCode를 principal에서 받아와 모임 동네로 clubDTO에 set
        long membersLocation = c.getMemberDTO().getMemAddressDTO().getLocationCode();
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocationCode(membersLocation);
        clubDTO.setLocation(locationDTO);

        // insertForm에서 받아온 카테고리 코드를 clubDTO에 set
        clubDTO.setCategory(categoryDTO);

        System.out.println("###### insert할 모임 데이터: " + clubDTO);

        // 저장 경로 설정
        String path = Paths.get("src", "main", "resources", "static", "uploadFiles").toAbsolutePath().normalize().toString();
        String filePath = path + File.separator; // 운영 체제에 맞는 구분자 추가
        System.out.println("###### 파일 저장 경로: " + filePath);

        // 이미지 이름 변경 처리
        String savedName = "";

        if(!img.isEmpty()) {

            // filePath에 폴더가 없으면 폴더 생성
            File mkdir = new File(filePath);
            if(!mkdir.exists()) {
                mkdir.mkdirs();
            }

            // 첨부 파일의 이름을 가져와서
            String originFileName = img.getOriginalFilename();
            // 확장자만 잘라서 변수에 저장
            String ext = originFileName.substring(originFileName.lastIndexOf("."));
            // 랜덤한 파일이름을 생성한 후 뒤에 확장자 추가
            savedName = UUID.randomUUID().toString().replace("-", "") + ext;

            // attachmentDTO에 원래 파일명과 변경한 파일명을 set
            AttachmentDTO attachmentDTO = new AttachmentDTO();
            attachmentDTO.setOriginalName(originFileName);
            attachmentDTO.setSavedName(savedName);
            attachmentDTO.setTypeCode("CL");

            // 리스트에 그 attachmentDTO를 add
            // 이미지 한 개라 index는 0 밖에 없음
            List<AttachmentDTO> attachmentList = new ArrayList<>();
            attachmentList.add(attachmentDTO);

            // attachmentList를 clubDTO에 set
            clubDTO.setAttachment(attachmentList);

            try {

                // 이미지 파일을 생성
                img.transferTo(new File(filePath + "/" + savedName));

            } catch (Exception e) {

                e.printStackTrace();

                // 오류 발생 시 생성한 파일을 삭제
                new File(filePath + "/" + savedName).delete();

                model.addAttribute("msg", "이미지 업로드 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        System.out.println("###### insert할 모임 데이터: " + clubDTO);

        try {

            // service의 모임 insert 메소드 호출
            clubService.insertClub(clubDTO);

            // mapper의 useGeneratedKeys="true" keyProperty="clubNo" 때문에 clubDTO에 insert한 모임의 No(PK)가 담김
            int clubNo = clubDTO.getClubNo();

            // 생성한 모임의 홈으로 바로 이동
            return "redirect:/club/clubHome?clubNo="+clubNo;

        } catch (Exception e) {

            e.printStackTrace();

            // 오류 발생 시 생성한 파일을 삭제
            new File(filePath + "/" + savedName).delete();

            model.addAttribute("msg", "모임 생성 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    /**
     * 모임 홈 조회
     *
     * @param c          the c
     * @param requestDTO the request dto
     * @param model      the model
     * @return the string
     * @since 2024 -07-26
     */
    @RequestMapping("/clubHome")
    public String clubHome(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model, RedirectAttributes redirectAttributes) {

        // 로그인된 유저의 no를 requestDTO에 set (가입 상태 확인 위해)
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            redirectAttributes.addFlashAttribute("alert", "해체된 모임입니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/club/clubMain?page=1";
        }

        System.out.println("###### 상세 조회할 모임 no: " + requestDTO.getClubNo());

        try {

            ClubDTO clubDTO = clubService.selectClubHome(requestDTO);
            System.out.println("###### 가져온 clubHome: " + clubDTO);
            model.addAttribute("clubHome", clubDTO);

            return "club/clubHome";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "모임 홈 조회 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    /**
     * 모임 가입 신청
     *
     * @param c                  the c
     * @param requestDTO         the request dto
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-27
     */
    @PostMapping("/requestJoinClub")
    public String requestJoinClub(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model, RedirectAttributes redirectAttributes) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 회원이 모임에 가입 신청을 하거나 이미 가입되지 않았는지 한 번 더 검증
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        try {

            // 서버단에서 현재 인원이 최대 인원보다 적은지 한 번 더 검증
            ClubDTO personnel = clubService.checkPersonnel(clubNo);
            int currentPersonnel = personnel.getCurrentPersonnel();
            int maxPersonnel = personnel.getMaxPersonnel();
            System.out.println("###### 현재 모임 인원: " + currentPersonnel + " / " + maxPersonnel);

            // 가입 안 돼 있고, 정원 가득 차지 않았을 때만 가입 신청 메소드 호출
            if(joinStatus.equals("N") && currentPersonnel < maxPersonnel) {

                clubService.requestJoinClub(requestDTO);

                redirectAttributes.addFlashAttribute("alert", "가입 신청이 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            }

            return "redirect:/club/clubHome?clubNo=" + clubNo;

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "모임 가입 신청 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    /**
     * 모임 가입 신청 취소
     *
     * @param c                  the c
     * @param requestDTO         the request dto
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-28
     */
    @PostMapping("/cancelJoinRequest")
    public String cancelJoinRequest(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model, RedirectAttributes redirectAttributes) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 회원이 모임 가입 대기 중인지 한 번 더 체크
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 가입 신청 후 대기 상태일 때만 가입 신청 취소 메소드 호출
        if(joinStatus.equals("W")) {

            try {

                clubService.cancelJoinRequest(requestDTO);

                redirectAttributes.addFlashAttribute("alert", "가입 신청이 취소되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            } catch (Exception e) {

                e.printStackTrace();
                model.addAttribute("msg", "모임 가입 신청 취소 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    /**
     * 모임 탈퇴
     *
     * @param c                  the c
     * @param requestDTO         the request dto
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-28
     */
    @PostMapping("/leaveClub")
    public String leaveClub(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model, RedirectAttributes redirectAttributes) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 회원이 가입된 상태인지 한 번 더 검증
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 가입된 상태일 때만 모임 탈퇴 메소드 호출
        if(joinStatus.equals("Y")) {

            try{

                clubService.leaveClub(requestDTO);

                redirectAttributes.addFlashAttribute("alert", "모임 탈퇴가 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            } catch (Exception e) {

                e.printStackTrace();
                model.addAttribute("msg", "모임 탈퇴 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    /**
     * 모임 해체
     *
     * @param c                  the c
     * @param requestDTO         the request dto
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-28
     */
    @PostMapping("/deleteClub")
    public String deleteClub(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model, RedirectAttributes redirectAttributes) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
        String isMaster = checkClubMaster(memberNo, clubNo);
        System.out.println("###### 모임 해체를 해체한 유저의 모임장 여부: " + isMaster);

        // 모임장이거나 관리자일 때만 모임 해체 메소드 호출
        if (isMaster.equals("Y") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

            try {

                clubService.deleteClub(requestDTO);

                redirectAttributes.addFlashAttribute("alert", "모임 해체가 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

                return "redirect:/club/clubMain?page=1";

            } catch (Exception e) {

                e.printStackTrace();
                model.addAttribute("msg", "모임 해체 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    /**
     * 모임 신고폼으로 이동
     *
     * @param c          the c
     * @param requestDTO the request dto
     * @param model      the model
     * @return the string
     * @since 2024 -07-31
     */
    @RequestMapping("/clubReportForm")
    public String clubReportForm(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        try {

            // service의 모임 홈 select 메소드 호출
            ClubDTO clubDTO = clubService.selectClubHome(requestDTO);

            System.out.println("###### 가져온 clubHome: " + clubDTO);

            model.addAttribute("clubHome", clubDTO);

            return "club/clubReportForm";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "모임 신고폼 이동 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    /**
     * 모임 신고
     *
     * @param c                  the c
     * @param reportDTO          the report dto
     * @param customReason       the custom reason
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -07-31
     */
    @PostMapping("/reportClub")
    public String reportClub(@AuthenticationPrincipal CustomUserDetails c, ReportDTO reportDTO, @RequestParam("customReason") String customReason, Model model, RedirectAttributes redirectAttributes) {

        int reportedNo = reportDTO.getReportedNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, reportedNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 로그인된 유저의 no를 신고자로 reportDTO에 set
        int reporterMemberNo = c.getMemberDTO().getMemberNo();
        reportDTO.setReporterMember(reporterMemberNo);

        // 신고 당하는 대상의 타입 코드를 reportDTO에 set
        reportDTO.setTypeCode("CL");

        // 신고 사유를 확인하여 custom이면 따로 파라미터로 받은 직접 입력 사유를 reportDTO에 set
        String reason = reportDTO.getReason();
        if (reason.equals("custom")) {
            reportDTO.setReason(customReason);
        }
        System.out.println("###### 신고 사유: " + reason);

        // 관리자 페이지에서 신고 대상 상세 조회로 갈 때 사용할 url을 reportDTO에 set
        String url = "/club/clubHome?clubNo=" + reportedNo;
        reportDTO.setUrl(url);

        try {

            // 신고 당하는 모임의 모임장을 피신고자로 reportDTO에 set
            int reportedMemberNo = clubService.checkClubMaster(reportedNo);
            reportDTO.setReportedMember(reportedMemberNo);

            // service의 모임 신고 메서드 호출
            clubService.reportClub(reportDTO);

            redirectAttributes.addFlashAttribute("alert", "모임 신고가 완료되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "success");

            return "redirect:/club/clubHome?clubNo=" + reportedNo;

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "모임 신고 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    /**
     * 모임 수정폼으로 이동
     *
     * @param c          the c
     * @param requestDTO the request dto
     * @param model      the model
     * @return the string
     * @since 2024 -08-02
     */
    @RequestMapping("/clubUpdateForm")
    public String clubUpdateForm(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
        String isMaster = checkClubMaster(memberNo, clubNo);

        // 모임장일 때만 모임 수정폼으로 이동
        if(isMaster.equals("Y")) {

            try {

                ClubDTO clubDTO = clubService.selectClubHome(requestDTO);

                System.out.println("###### 가져온 clubHome: " + clubDTO);

                model.addAttribute("clubHome", clubDTO);

                return "club/clubUpdateForm";

            } catch (Exception e) {

                e.printStackTrace();
                model.addAttribute("msg", "모임 수정폼 이동 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    /**
     * 모임 수정
     *
     * @param c                  the c
     * @param categoryDTO        the category dto
     * @param clubDTO            the club dto
     * @param img                the img
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -08-02
     */
    @PostMapping("/updateClub")
    public String updateClub(@AuthenticationPrincipal CustomUserDetails c, CategoryDTO categoryDTO, ClubDTO clubDTO, @RequestParam("img") MultipartFile img, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();

        int clubNo = clubDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
        String isMaster = checkClubMaster(memberNo, clubNo);

        // 모임장일 때만 모임 수정
        if(isMaster.equals("Y")) {

            List<AttachmentDTO> beforeAttachment = new ArrayList<>();

            try {

                // 추후 이미지를 새로 업로드 했을 때 기존 이미지 삭제를 위해 기존 이미지 select
                beforeAttachment = clubService.selectClubImg(clubNo);

            } catch (Exception e) {

                e.printStackTrace();
                model.addAttribute("msg", "모임 수정 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

            // updateForm에서 받아온 카테고리 코드를 clubDTO에 set
            clubDTO.setCategory(categoryDTO);

            // 저장 경로 설정
            String path = Paths.get("src", "main", "resources", "static", "uploadFiles").toAbsolutePath().normalize().toString();
            String filePath = path + File.separator; // 운영 체제에 맞는 구분자 추가

            System.out.println("###### 파일 저장 경로: " + filePath);

            // 이미지 이름 변경 처리
            String savedName = "";

            if(!img.isEmpty()) {

                // filePath에 폴더가 없으면 폴더 생성
                File mkdir = new File(filePath);
                if(!mkdir.exists()) {
                    mkdir.mkdirs();
                }

                // 첨부 파일의 이름을 가져와서
                String originFileName = img.getOriginalFilename();
                // 확장자만 잘라서 변수에 저장
                String ext = originFileName.substring(originFileName.lastIndexOf("."));
                // 랜덤한 파일이름을 생성한 후 뒤에 확장자 추가
                savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                // attachmentDTO에 원래 파일명과 변경한 파일명을 set
                AttachmentDTO attachmentDTO = new AttachmentDTO();
                attachmentDTO.setOriginalName(originFileName);
                attachmentDTO.setSavedName(savedName);
                attachmentDTO.setTypeCode("CL");

                // 리스트에 그 attachmentDTO를 add
                // 이미지 한 개라 index는 0 밖에 없음
                List<AttachmentDTO> attachmentList = new ArrayList<>();
                attachmentList.add(attachmentDTO);

                // attachmentList를 clubDTO에 set
                clubDTO.setAttachment(attachmentList);

                try {

                    // 이미지 파일을 생성
                    img.transferTo(new File(filePath + "/" + savedName));

                } catch (Exception e) {

                    e.printStackTrace();

                    // 오류 발생 시 생성한 파일을 삭제
                    new File(filePath + "/" + savedName).delete();

                    model.addAttribute("msg", "이미지 업로드 과정에서 문제가 발생했습니다.");

                    return "common/errorPage";

                }

            }

            try {

                int result = clubService.updateClub(clubDTO);

                // 새 이미지를 업로드 했을 때 기존 이미지 삭제
                if(result == 2) {

                    System.out.println("###### 삭제할 기존 이미지: " + beforeAttachment);

                    String beforeSavedName = beforeAttachment.get(0).getSavedName();

                    new File(filePath + "/" + beforeSavedName).delete();

                }

                redirectAttributes.addFlashAttribute("alert", "모임 수정이 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            } catch (Exception e) {

                e.printStackTrace();

                // 오류 발생 시 생성한 파일을 삭제
                new File(filePath + "/" + savedName).delete();

                model.addAttribute("msg", "모임 수정 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    @RequestMapping("/clubLog/logMain")
    public String clubLog(@AuthenticationPrincipal CustomUserDetails c, FilterDTO filterDTO, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = filterDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 로그인된 유저가 가입된 상태인지 한 번 더 확인
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 가입된 상태 또는 관리자일 때만 기록 페이지로 이동
        if (joinStatus.equals("Y") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

            try {

                // 받아온 page로 시작 및 시작 index를 설정
                filterDTO.setStartAndStartIndex(filterDTO.getPage());

                // 검색 조건 확인
                System.out.println("###### 검색하는 검색어: " + filterDTO.getSearchWord());

                // 로그 리스트 select 메소드 호출
                List<LogDTO> logList = clubService.selectLogList(filterDTO);
                System.out.println("###### 가져온 기록 리스트: " + logList);
                model.addAttribute("logList", logList);

                // 로그 개수 받아와 페이지 수 계산
                int logCount = clubService.selectLogCount(filterDTO);
                System.out.println("###### 기록 총 개수: " + logCount);

                int pages = 0;

                if (logCount != 0) {

                    pages = logCount / 5;

                    if (logCount % 5 != 0) {
                        pages += 1;
                    }

                }

                System.out.println("###### 페이지 개수: " + pages);

                model.addAttribute("pages", pages);
                model.addAttribute("clubNo", clubNo);
                model.addAttribute("filter", filterDTO);

                String isClubDeleted = clubService.checkIsClubDeleted(clubNo);
                model.addAttribute("isClubDeleted", isClubDeleted);

                return "/club/clubLog/logMain";

            } catch (Exception e) {

                e.printStackTrace();
                model.addAttribute("msg", "모임 기록 리스트 조회 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        // 미가입인 상태일 때는 모임 홈으로 이동
        redirectAttributes.addFlashAttribute("alert", "모임 기록은 모임 멤버만 조회할 수 있습니다.");
        redirectAttributes.addFlashAttribute("alertType", "warning");

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    /**
     * 기록 작성폼으로 이동
     *
     * @param c      the c
     * @param clubNo the club no
     * @param model  the model
     * @return the string
     * @since 2024 -08-02
     */
    @RequestMapping("/clubLog/logInsertForm")
    public String logInsertForm(@AuthenticationPrincipal CustomUserDetails c, @RequestParam("clubNo") int clubNo, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 로그인된 유저가 가입된 상태인지 한 번 더 확인
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 가입된 상태일 때만 기록 작성폼으로 이동
        if(joinStatus.equals("Y")) {

            model.addAttribute("clubNo", clubNo);

            return "/club/clubLog/logInsertForm";

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    /**
     * 기록 작성
     *
     * @param c       the c
     * @param logDTO  the log dto
     * @param imgList the img list
     * @param model   the model
     * @return the string
     * @since 2024 -08-02
     */
    @PostMapping("/clubLog/insertLog")
    public String insertLog(@AuthenticationPrincipal CustomUserDetails c, LogDTO logDTO, @RequestParam("imgList") MultipartFile[] imgList, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = logDTO.getClubNo();

        // 해체된 모임인지 검증
        if(!checkIsClubDeleted(c, clubNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 회원이 가입된 상태인지 한 번 더 검증
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 가입된 상태일 때만 기록 작성
        if(joinStatus.equals("Y")) {

            // 로그인된 유저를 작성자로 set
            ClubMemberDTO writer = new ClubMemberDTO();
            writer.setMemberNo(memberNo);
            logDTO.setWriter(writer);

            // 저장 경로 설정
            String path = Paths.get("src", "main", "resources", "static", "uploadFiles").toAbsolutePath().normalize().toString();
            String filePath = path + File.separator; // 운영 체제에 맞는 구분자 추가

            System.out.println("###### 파일 저장 경로: " + filePath);

            System.out.println("###### img 개수 >>>>> " + imgList.length);

            List<String> savedNameList = new ArrayList<>();

            if(imgList.length > 0) {

                // filePath에 폴더가 없으면 폴더 생성
                File mkdir = new File(filePath);
                if(!mkdir.exists()) {
                    mkdir.mkdirs();
                }

                List<AttachmentDTO> attachmentList = new ArrayList<>();

                for(MultipartFile img : imgList) {

                    if(!img.isEmpty()) {

                        // 첨부 파일의 이름을 가져와서
                        String originFileName = img.getOriginalFilename();
                        // 확장자만 잘라서 변수에 저장
                        String ext = originFileName.substring(originFileName.lastIndexOf("."));
                        // 랜덤한 파일이름을 생성한 후 뒤에 확장자 추가
                        String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                        AttachmentDTO attachment = new AttachmentDTO();
                        attachment.setOriginalName(originFileName);
                        attachment.setSavedName(savedName);
                        attachment.setTypeCode("CL-LOG");

                        attachmentList.add(attachment);
                        savedNameList.add(savedName);

                        try {

                            img.transferTo(new File(filePath + "/" + savedName));

                        } catch (Exception e) {

                            e.printStackTrace();

                            // 오류 발생 시 생성한 파일을 삭제
                            new File(filePath + "/" + savedName).delete();

                            model.addAttribute("msg", "이미지 업로드 과정에서 문제가 발생했습니다.");

                            return "common/errorPage";

                        }

                    }

                }

                logDTO.setAttachments(attachmentList);

            }

            System.out.println("###### 작성할 기록 >>>>> " + logDTO);

            try {

                clubService.insertLog(logDTO);
                int logNo = logDTO.getLogNo();

                return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;

            } catch (Exception e) {

                e.printStackTrace();

                // 오류 발생 시 생성한 파일을 삭제
                for(String savedName : savedNameList) {

                    new File(filePath + "/" + savedName).delete();

                }

                model.addAttribute("msg", "모임 기록 작성 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        // 미가입 상태일 때 모임 홈으로 이동
        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    @RequestMapping("/clubLog/logDetail")
    public String logDetail(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();
        int logNo = requestDTO.getLogNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 모임에 가입된 상태인지 한 번 더 확인
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 모임 가입자나 관리자일 때만 기록 상세 조회 메소드 호출
        if (joinStatus.equals("Y") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

            try {

                clubService.addLogViews(logNo);

                LogDTO logDetail = clubService.selectLogDetail(requestDTO);

                System.out.println("###### 가져온 기록 상세 >>>>> " + logDetail);

                ClubMemberDTO clubMemberDTO = logDetail.getWriter();
                String profileSavedName = clubMemberDTO.getProfileSavedName();

                // 작성자의 프로필 사진이 없거나 디폴트라면 기본 이미지 이름으로 savedName을 set
                if (profileSavedName == null || profileSavedName.equals("") || profileSavedName.equals("defaultProfile.png")) {

                    clubMemberDTO.setProfileSavedName("defaultProfile.png");
                    logDetail.setWriter(clubMemberDTO);

                }

                model.addAttribute("logDetail", logDetail);

                return "club/clubLog/logDetail";

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "모임 기록 상세 조회 과정에서 문제가 발생했습니다.");

                return "common/errorPage";
            }

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    @PostMapping("/clubLog/insertReply")
    public String insertReply(@AuthenticationPrincipal CustomUserDetails c, @RequestParam("clubNo") int clubNo, ReplyDTO replyDTO, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();

        int logNo = replyDTO.getLogNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 서버단에서 가입돼 있는지 한 번 더 확인
        String joinStatus = checkJoinStatus(memberNo,clubNo);

        if(joinStatus.equals("Y")) {

            ClubMemberDTO writer = new ClubMemberDTO();
            writer.setMemberNo(memberNo);
            replyDTO.setWriter(writer);

            try {

                clubService.insertReply(replyDTO);

                return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;

            } catch (Exception e) {

                e.printStackTrace();
                model.addAttribute("msg", "댓글 작성 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        // 미가입 상태일 때 모임 홈으로 이동
        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    @PostMapping("/clubLog/updateReply")
    public String updateReply(@AuthenticationPrincipal CustomUserDetails c, @RequestParam("clubNo") int clubNo, ReplyDTO replyDTO, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int logNo = replyDTO.getLogNo();
        int replyNo = replyDTO.getReplyNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        String joinStatus = checkJoinStatus(memberNo, clubNo);
        String isReplyWriter = checkReplyWriter(memberNo, replyNo);

        if(joinStatus.equals("Y") && isReplyWriter.equals("Y")) {

            try {

                int result = clubService.updateReply(replyDTO);

                if(result == 1) {
                    redirectAttributes.addFlashAttribute("alert", "댓글 수정이 완료되었습니다.");
                    redirectAttributes.addFlashAttribute("alertType", "success");
                }

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "댓글 수정 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;

    }


    @PostMapping("/clubLog/deleteReply")
    public String deleteReply(@AuthenticationPrincipal CustomUserDetails c, @RequestParam("clubNo") int clubNo, ReplyDTO replyDTO, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int logNo = replyDTO.getLogNo();
        int replyNo = replyDTO.getReplyNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        String isReplyWriter = checkReplyWriter(memberNo, replyNo);

        if(isReplyWriter.equals("Y") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

            try {

                int result = clubService.deleteReply(replyDTO);

                if(result == 1) {
                    redirectAttributes.addFlashAttribute("alert", "댓글 삭제가 완료되었습니다.");
                    redirectAttributes.addFlashAttribute("alertType", "success");
                }

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "댓글 삭제 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;

    }


    @RequestMapping("/clubLog/logReportForm")
    public String logReportForm(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, requestDTO.getClubNo(), requestDTO.getLogNo())) {
            return "redirect:/club/clubMain?page=1";
        }

        try {

            LogDTO reportedLog = clubService.selectLogDetail(requestDTO);

            model.addAttribute("reportedLog", reportedLog);

            return "/club/clubLog/logReportForm";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "기록 신고폼 이동 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    @PostMapping("/clubLog/reportLog")
    public String reportlog(@AuthenticationPrincipal CustomUserDetails c, ReportDTO reportDTO, @RequestParam("customReason") String customReason, @RequestParam("clubNo") int clubNo, Model model, RedirectAttributes redirectAttributes) {

        int reportedNo = reportDTO.getReportedNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, reportedNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        // 로그인된 유저의 no를 신고자로 reportDTO에 set
        int reporterMemberNo = c.getMemberDTO().getMemberNo();
        reportDTO.setReporterMember(reporterMemberNo);

        // 신고 당하는 대상의 타입 코드를 reportDTO에 set
        reportDTO.setTypeCode("CL-LOG");

        // 신고 사유를 확인하여 custom이면 따로 파라미터로 받은 직접 입력 사유를 reportDTO에 set
        String reason = reportDTO.getReason();
        if(reason.equals("custom")) {
            reportDTO.setReason(customReason);
        }
        System.out.println("###### 신고 사유: " + reason);

        // 관리자 페이지에서 신고 대상 상세 조회로 갈 때 사용할 url을 reportDTO에 set
        String url = "/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + reportedNo;
        reportDTO.setUrl(url);

        try {

            // 신고 당하는 기록의 작성자를 피신고자로 reportDTO에 set
            int reportedMemberNo = clubService.checkLogWriter(reportedNo);
            reportDTO.setReportedMember(reportedMemberNo);

            clubService.reportLog(reportDTO);

            redirectAttributes.addFlashAttribute("alert", "기록 신고가 완료되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "success");

            return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + reportedNo;

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "모임 신고 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    @RequestMapping("/clubLog/logUpdateForm")
    public String logUpdateForm(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();
        int logNo = requestDTO.getLogNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        String isLogWriter = checkLogWriter(memberNo, logNo);

        if(isLogWriter.equals("Y")) {

            try {

                LogDTO logDetail = clubService.selectLogDetail(requestDTO);

                model.addAttribute("logDetail", logDetail);

                return "/club/clubLog/logUpdateForm";

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "기록 수정폼 이동 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;

    }


    @PostMapping("/clubLog/updateLog")
    public String updateLog(@AuthenticationPrincipal CustomUserDetails c, LogDTO logDTO, @RequestParam("imgList") MultipartFile[] imgList, @RequestParam("status[]") int[] status, @RequestParam("fileNo[]") int[] fileNo, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = logDTO.getClubNo();
        int logNo = logDTO.getLogNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        String isLogWriter = checkLogWriter(memberNo, logNo);
        if(isLogWriter.equals("Y")) {

            try {

                int logUpdateResult = clubService.updateLog(logDTO);

                if(logUpdateResult == 1) {

                    System.out.println("###### fileNo 배열: " + Arrays.toString(fileNo));
                    System.out.println("###### status 배열: " + Arrays.toString(status));

                    // 이미지 저장할 경로 설정
                    String root = "/Users/hyeoni/Desktop/workspace/multiit/final_udonghaeng/udong/src/main/resources/static";
                    String filePath = root + "/uploadFiles";

                    for(int i = 0; i < 4; i++) {

                        // 기존 파일이 있어 fileNo가 0보다 크고 status가 -2로 새 이미지를 업로드한 경우
                        // 기존 fileNo를 update
                        if(fileNo[i] > 0 && status[i] == -2) {

                            // update 성공 시 기존 이미지 제거를 위해 select
                            AttachmentDTO beforeImg = clubService.selectAttachment(fileNo[i]);
                            String beforeSavedName = beforeImg.getSavedName();

                            String originFileName = imgList[i].getOriginalFilename();
                            String ext = originFileName.substring(originFileName.lastIndexOf("."));
                            String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                            AttachmentDTO newImg = new AttachmentDTO();
                            newImg.setOriginalName(originFileName);
                            newImg.setSavedName(savedName);
                            newImg.setFileNo(fileNo[i]);
                            newImg.setTypeCode("CL-LOG");
                            newImg.setTargetNo(logNo);

                            try {

                                imgList[i].transferTo(new File(filePath + "/" + savedName));

                                int updateAttachmentResult = clubService.updateAttachment(newImg);

                                if(updateAttachmentResult == 1) {

                                    // update 성공 시 기존 이미지 제거
                                    new File(filePath + "/" + beforeSavedName).delete();

                                }
                                else {

                                    // update 실패 시 새로 생성한 이미지 제거
                                    new File(filePath + "/" + savedName).delete();

                                }

                            } catch (Exception e) {

                                e.printStackTrace();

                                // 오류 발생 시 생성한 파일을 삭제한다.
                                new File(filePath + "/" + savedName).delete();

                                model.addAttribute("msg", "기록 수정 과정에서 문제가 발생했습니다.");

                                return "common/errorPage";

                            }


                        }
                        // 기존 파일이 있어 fileNo가 0보다 크고 status가 -3으로 이미지를 삭제한 경우
                        // 기존 fileNo를 delete
                        else if(fileNo[i] > 0 && status[i] == -3) {

                            // delete 성공 시 기존 이미지 제거를 위해 select
                            AttachmentDTO beforeImg = clubService.selectAttachment(fileNo[i]);
                            String beforeSavedName = beforeImg.getSavedName();

                            AttachmentDTO deletedImg = new AttachmentDTO();

                            deletedImg.setFileNo(fileNo[i]);
                            deletedImg.setTypeCode("CL-LOG");
                            deletedImg.setTargetNo(logNo);

                            int deleteAttachmentResult = clubService.deleteAttachment(deletedImg);

                            if(deleteAttachmentResult == 1) {

                                // delete 성공 시 기존 이미지 제거
                                new File(filePath + "/" + beforeSavedName).delete();

                            }

                        }
                        // 기존 파일이 없이 fileNo가 0이고 status가 -2로 새 이미지를 업로드한 경우
                        // 새 이미지를 insert
                        else if(fileNo[i] == 0 && status[i] == -2) {

                            String originFileName = imgList[i].getOriginalFilename();
                            String ext = originFileName.substring(originFileName.lastIndexOf("."));
                            String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                            AttachmentDTO newImg = new AttachmentDTO();
                            newImg.setOriginalName(originFileName);
                            newImg.setSavedName(savedName);
                            newImg.setTypeCode("CL-LOG");
                            newImg.setTargetNo(logNo);

                            try {

                                imgList[i].transferTo(new File(filePath + "/" + savedName));

                                clubService.insertAttachment(newImg);

                            } catch (Exception e) {

                                e.printStackTrace();

                                // 오류 발생 시 생성한 파일을 삭제한다.
                                new File(filePath + "/" + savedName).delete();

                                model.addAttribute("msg", "기록 수정 과정에서 문제가 발생했습니다.");

                                return "common/errorPage";

                            }

                        }

                    }

                }

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "기록 수정 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

            redirectAttributes.addFlashAttribute("alert", "기록 수정이 완료되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "success");

        }

        return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;

    }


    @PostMapping("/clubLog/deleteLog")
    public String deleteLog(@AuthenticationPrincipal CustomUserDetails c, LogDTO logDTO, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = logDTO.getClubNo();
        int logNo = logDTO.getLogNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return "redirect:/club/clubMain?page=1";
        }

        String isLogWriter = checkLogWriter(memberNo, logNo);

        if(isLogWriter.equals("Y") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

            try {

                clubService.deleteLog(logDTO);

                redirectAttributes.addFlashAttribute("alert", "기록 삭제가 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

                return "redirect:/club/clubLog/logMain?clubNo=" + clubNo;

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "기록 삭제 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        // 작성자나 관리자가 아니면 기록 상세 페이지로 이동
        return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;

    }


    @PostMapping("/clubLog/insertLogLike")
    public ResponseEntity<Void> insertLogLike(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = requestDTO.getClubNo();
        int logNo = requestDTO.getLogNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        System.out.println("###### 현재 clubNo: " + clubNo);
        System.out.println("###### 좋아요할 logNo: " + logNo);

        String joinStatus = checkJoinStatus(memberNo, clubNo);

        if(joinStatus.equals("Y")) {

            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setMemberNo(memberNo);
            likeDTO.setLogNo(logNo);

            try {

                clubService.insertLogLike(likeDTO);

                System.out.println("###### 좋아요 성공!");


            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/clubLog/deleteLogLike")
    public ResponseEntity<Void> deleteLogLike(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = requestDTO.getClubNo();
        int logNo = requestDTO.getLogNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, logNo)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        System.out.println("###### 현재 clubNo: " + clubNo);
        System.out.println("###### 좋아요 취소할 logNo: " + logNo);

        String joinStatus = checkJoinStatus(memberNo, clubNo);

        if(joinStatus.equals("Y")) {

            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setMemberNo(memberNo);
            likeDTO.setLogNo(logNo);

            try {

                clubService.deleteLogLike(likeDTO);

                System.out.println("###### 좋아요 취소!");


            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/clubLog/insertReplyLike")
    public ResponseEntity<Void> insertReplyLike(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = requestDTO.getClubNo();
        int replyNo = requestDTO.getReplyNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, requestDTO.getLogNo())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        System.out.println("###### 현재 clubNo: " + clubNo);
        System.out.println("###### 좋아요할 replyNo: " + replyNo);

        String joinStatus = checkJoinStatus(memberNo, clubNo);

        if(joinStatus.equals("Y")) {

            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setMemberNo(memberNo);
            likeDTO.setReplyNo(replyNo);

            try {

                clubService.insertReplyLike(likeDTO);

                System.out.println("###### 좋아요 성공!");


            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/clubLog/deleteReplyLike")
    public ResponseEntity<Void> deleteReplyLike(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int clubNo = requestDTO.getClubNo();
        int replyNo = requestDTO.getReplyNo();

        // 해체된 모임인지, 삭제된 기록인지 검증
        if(!checkIsLogDeleted(c, clubNo, requestDTO.getLogNo())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        System.out.println("###### 현재 clubNo: " + clubNo);
        System.out.println("###### 좋아요 취소할 replyNo: " + replyNo);

        String joinStatus = checkJoinStatus(memberNo, clubNo);

        if(joinStatus.equals("Y")) {

            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setMemberNo(memberNo);
            likeDTO.setReplyNo(replyNo);

            try {

                clubService.deleteReplyLike(likeDTO);

                System.out.println("###### 좋아요 취소!");


            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return new ResponseEntity<>(HttpStatus.OK);

    }


    // ================= 공통 사용 메소드 =================

    /**
     * 유저가 모임에 가입돼 있는지 확인
     *
     * @param memberNo the member no
     * @param clubNo   the club no
     * @return the string
     * @since 2024 -08-02
     */
    public String checkJoinStatus(int memberNo, int clubNo) {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setMemberNo(memberNo);
        requestDTO.setClubNo(clubNo);

        try {

            String joinStatus = clubService.checkJoinStatus(requestDTO);

            if(joinStatus == null) {
                joinStatus = "N";
            }

            return joinStatus;

        } catch (Exception e) {

            e.printStackTrace();

            return "common/errorPage";

        }

    }


    /**
     * 유저가 모임장인지 확인
     *
     * @param memberNo the member no
     * @param clubNo   the club no
     * @return the string
     * @since 2024 -08-02
     */
    public String checkClubMaster(int memberNo, int clubNo) {

        String isMaster = "N";

        try {

            int masterNo = clubService.checkClubMaster(clubNo);

            if(masterNo == memberNo) {
                isMaster = "Y";
            }

            return isMaster;

        } catch (Exception e) {

            e.printStackTrace();

            return "common/errorPage";

        }

    }

    public boolean checkIsClubDeleted(@AuthenticationPrincipal CustomUserDetails c, int clubNo) {

        try {

            String isClubDeleted = clubService.checkIsClubDeleted(clubNo);

            if(isClubDeleted == null || isClubDeleted.equals("")) {

                return false;

            }

            if(isClubDeleted.equals("N") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

                return true;

            }
            else {

                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        }

    }

    public boolean checkIsLogDeleted(@AuthenticationPrincipal CustomUserDetails c, int clubNo, int logNo) {

        if(!checkIsClubDeleted(c, clubNo)) {

            return false;

        }

        try {

            String isLogDeleted = clubService.checkIsLogDeleted(logNo);

            if(isLogDeleted == null || isLogDeleted.equals("")) {

                return false;

            }

            if(isLogDeleted.equals("N") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

                return true;

            }
            else {

                return false;

            }

        } catch (Exception e) {

            return false;

        }

    }

    public String checkLogWriter(int memberNo, int logNo) {

        String isLogWriter = "N";

        try {

            int logWriter = clubService.checkLogWriter(logNo);

            if(logWriter == memberNo) {

                isLogWriter = "Y";

            }

            return isLogWriter;

        } catch (Exception e) {

            e.printStackTrace();

            return "common/errorPage";

        }


    }

    public String checkReplyWriter(int memberNo, int replyNo) {

        String isReplyWriter = "N";

        try {

            int replyWriter = clubService.checkReplyWriter(replyNo);

            if(replyWriter == memberNo) {
                isReplyWriter = "Y";
            }

            return isReplyWriter;

        } catch (Exception e) {

            e.printStackTrace();

            return "common/errorPage";

        }

    }


}
