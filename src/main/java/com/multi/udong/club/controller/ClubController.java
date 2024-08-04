package com.multi.udong.club.controller;

import com.multi.udong.club.model.dto.*;
import com.multi.udong.club.service.ClubService;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import com.multi.udong.login.service.CustomUserDetails;
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
import java.util.ArrayList;
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

        // 이미지 저장할 경로 설정
        String root = "/Users/hyeoni/Desktop/workspace/multiit/final_udonghaeng/udong/src/main/resources/static";
        String filePath = root + "/uploadFiles";

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
    public String clubHome(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        System.out.println("###### 상세 조회할 모임 no: " + requestDTO.getClubNo());

        try {

            // service의 모임 홈 select 메소드 호출
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

        // 서버단에서 회원이 모임에 가입 신청을 하거나 이미 가입되지 않았는지 한 번 더 검증
        String joinStatus = checkJoinStatus(memberNo, clubNo);
        System.out.println("###### 가입 신청한 유저의 모임 가입 상태: " + joinStatus);

        try {

            // 서버단에서 현재 인원이 최대 인원보다 적은지 한 번 더 검증
            ClubDTO personnel = clubService.checkPersonnel(clubNo);
            int currentPersonnel = personnel.getCurrentPersonnel();
            int maxPersonnel = personnel.getMaxPersonnel();
            System.out.println("###### 현재 모임 인원: " + currentPersonnel + " / " + maxPersonnel);

            // 가입 안 돼 있고, 정원 가득 차지 않았을 때만 가입 신청 메소드 호출
            if(joinStatus.equals("N") && currentPersonnel < maxPersonnel) {

                clubService.requestJoinClub(requestDTO);

                redirectAttributes.addFlashAttribute("message", "가입 신청이 완료되었습니다.");

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

        // 서버단에서 회원이 모임 가입 대기 중인지 한 번 더 체크
        String joinStatus = checkJoinStatus(memberNo, clubNo);
        System.out.println("###### 가입 신청 취소한 유저의 모임 가입 상태: " + joinStatus);

        // 가입 신청 후 대기 상태일 때만 가입 신청 취소 메소드 호출
        if(joinStatus.equals("W")) {

            try {

                clubService.cancelJoinRequest(requestDTO);

                redirectAttributes.addFlashAttribute("message", "가입 신청이 취소되었습니다.");


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

        // 서버단에서 회원이 가입된 상태인지 한 번 더 검증
        String joinStatus = checkJoinStatus(memberNo, clubNo);
        System.out.println("###### 탈퇴 신청한 유저의 가입 상태: " + joinStatus);

        // 가입된 상태일 때만 모임 탈퇴 메소드 호출
        if(joinStatus.equals("Y")) {

            try{

                clubService.leaveClub(requestDTO);

                redirectAttributes.addFlashAttribute("message", "모임 탈퇴가 완료되었습니다.");

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

        // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
        String isMaster = checkClubMaster(memberNo, clubNo);
        System.out.println("###### 모임 해체를 해체한 유저의 모임장 여부: " + isMaster);

        // 서버단에서 로그인된 유저가 관리자인지 한 번 더 검증
        String isAdmin = checkAdmin(memberNo);
        System.out.println("###### 모임 해체를 해체한 유저의 관리자 여부: " + isAdmin);

        // 모임장이거나 관리자일 때만 모임 해체 메소드 호출
        if(isMaster.equals("Y") || isAdmin.equals("Y")) {

            try {

                // 추후 해체에 성공했을 때 이미지 삭제를 위해 이미지 select
                List<AttachmentDTO> attachment = clubService.selectClubImg(clubNo);

                int result = clubService.deleteClub(requestDTO);

                // 모임 해체 성공 시 이미지 삭제
                if(result == 1) {

                    System.out.println("###### 삭제할 이미지: " + attachment);

                    String savedName = attachment.get(0).getSavedName();

                    String root = "/Users/hyeoni/Desktop/workspace/multiit/final_udonghaeng/udong/src/main/resources/static";
                    String filePath = root + "/uploadFiles";

                    new File(filePath + "/" + savedName).delete();

                    redirectAttributes.addFlashAttribute("message", "모임 해체가 완료되었습니다.");

                    return "redirect:/club/clubMain?page=1";

                }

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

        System.out.println("###### 신고할 모임 no: " + requestDTO.getClubNo());

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

        // 로그인된 유저의 no를 신고자로 reportDTO에 set
        int reporterMemberNo = c.getMemberDTO().getMemberNo();
        reportDTO.setReporterMember(reporterMemberNo);

        // 신고 당하는 대상의 타입 코드를 reportDTO에 set
        reportDTO.setTypeCode("CL");

        // 신고 사유를 확인하여 custom이면 따로 파라미터로 받은 직접 입력 사유를 reportDTO에 set
        String reason = reportDTO.getReason();
        if(reason.equals("custom")) {
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

            redirectAttributes.addFlashAttribute("message", "모임 신고가 완료되었습니다.");

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

        // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
        String isMaster = checkClubMaster(memberNo, clubNo);
        System.out.println("###### 모임 수정을 시도한 유저 모임장 여부: " + isMaster);

        // 모임장일 때만 모임 수정폼으로 이동
        if(isMaster.equals("Y")) {

            try {

                // service의 모임 홈 select 메소드 호출
                ClubDTO clubDTO = clubService.selectClubHome(requestDTO);

                System.out.println("###### 가져온 clubHome: " + clubDTO);

                model.addAttribute("clubHome", clubDTO);

                return "club/clubUpdateForm";

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "모임 신고 과정에서 문제가 발생했습니다.");

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

        int clubNo = clubDTO.getClubNo();

        // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
        int memberNo = c.getMemberDTO().getMemberNo();
        String isMaster = checkClubMaster(memberNo, clubNo);

        // 모임장일 때만 모임 수정
        if(isMaster.equals("Y")) {

            List<AttachmentDTO> beforeAttachment = new ArrayList<>();

            // 추후 이미지를 새로 업로드 했을 때 기존 이미지 삭제를 위해 기존 이미지 select
            try {

                beforeAttachment = clubService.selectClubImg(clubNo);

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "모임 수정 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

            // updateForm에서 받아온 카테고리 코드를 clubDTO에 set
            clubDTO.setCategory(categoryDTO);

            // 이미지 저장할 경로 설정
            String root = "/Users/hyeoni/Desktop/workspace/multiit/final_udonghaeng/udong/src/main/resources/static";
            String filePath = root + "/uploadFiles";

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

                redirectAttributes.addFlashAttribute("message", "모임 수정이 완료되었습니다.");

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

        // 서버단에서 로그인된 유저가 가입된 상태인지 한 번 더 확인
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 가입된 상태일 때만 기록 페이지로 이동
        if(joinStatus.equals("Y")) {

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

                return "/club/clubLog/logMain";

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "모임 기록 리스트 조회 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        // 미가입인 상태일 때는 모임 홈으로 이동
        redirectAttributes.addFlashAttribute("message", "모임 기록은 모임 멤버만 조회할 수 있습니다.");

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

        // 서버단에서 로그인된 유저가 가입된 상태인지 한 번 더 확인
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

        // 서버단에서 회원이 가입된 상태인지 한 번 더 검증
        String joinStatus = checkJoinStatus(memberNo, clubNo);

        // 가입된 상태일 때만 기록 작성
        if(joinStatus.equals("Y")) {

            // 로그인된 유저를 작성자로 set
            ClubMemberDTO writer = new ClubMemberDTO();
            writer.setMemberNo(memberNo);
            logDTO.setWriter(writer);

            // 이미지 저장할 경로 설정
            String root = "/Users/hyeoni/Desktop/workspace/multiit/final_udonghaeng/udong/src/main/resources/static";
            String filePath = root + "/uploadFiles";

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

                int result = clubService.insertLog(logDTO);

                int logNo = logDTO.getLogNo();

                // return "redirect:/club/clubLog/logDetail?clubNo=" + clubNo + "&logNo=" + logNo;
                return "redirect:/club/clubLog/logMain?clubNo=" + clubNo;

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

            int materNo = clubService.checkClubMaster(clubNo);

            if(materNo == memberNo) {
                isMaster = "Y";
            }

            return isMaster;

        } catch (Exception e) {

            e.printStackTrace();

            return "common/errorPage";

        }

    }


    /**
     * 유저가 관리자인지 확인
     *
     * @param memberNo the member no
     * @return the string
     * @since 2024 -08-02
     */
    public String checkAdmin(int memberNo) {

        String isAdmin = "N";

        try {

            String authority = clubService.checkAdmin(memberNo);

            if(authority.equals("ROLE_ADMIN")) {
                isAdmin = "Y";
            }

            return isAdmin;

        } catch (Exception e) {

            e.printStackTrace();

            return "common/errorPage";

        }

    }

}
