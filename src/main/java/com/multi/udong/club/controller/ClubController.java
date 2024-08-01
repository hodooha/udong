package com.multi.udong.club.controller;

import com.multi.udong.club.model.dto.*;
import com.multi.udong.club.service.ClubService;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import com.multi.udong.security.CustomUserDetails;
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

        try {

            System.out.println("###### insert할 모임 데이터: " + clubDTO);

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

        try {

            // 서버단에서 회원이 모임에 가입 신청을 하거나 이미 가입되지 않았는지 한 번 더 검증
            String joinStatus = clubService.checkJoinStatus(requestDTO);
            System.out.println("###### 가입 신청한 유저의 가입 상태: " + joinStatus);

            // 서버단에서 현재 인원이 최대 인원보다 적은지 한 번 더 검증
            ClubDTO personnel = clubService.checkPersonnel(clubNo);
            int currentPersonnel = personnel.getCurrentPersonnel();
            int maxPersonnel = personnel.getMaxPersonnel();
            System.out.println("###### 현재 모임 인원: " + currentPersonnel + " / " + maxPersonnel);

            // 가입 안 돼 있고, 정원 가득 차지 않았을 때만 가입 신청 메소드 호출
            if(joinStatus == null && currentPersonnel < maxPersonnel) {

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

        try {

            // 서버단에서 회원이 가입 신청 후 대기 중 상태인지 한 번 더 검증
            String joinStatus = clubService.checkJoinStatus(requestDTO);
            System.out.println("###### 가입 신청 취소한 유저의 가입 상태: " + joinStatus);

            // 가입 신청 후 대기 상태일 때만 가입 신청 취소 메소드 호출
            if(joinStatus.equals("W")) {

                clubService.cancelJoinRequest(requestDTO);

                redirectAttributes.addFlashAttribute("message", "가입 신청이 취소되었습니다.");

            }

            return "redirect:/club/clubHome?clubNo=" + clubNo;

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "모임 가입 신청 취소 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

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

        try {

            // 서버단에서 회원이 가입된 상태인지 한 번 더 검증
            String joinStatus = clubService.checkJoinStatus(requestDTO);
            System.out.println("###### 탈퇴 신청한 유저의 가입 상태: " + joinStatus);

            // 가입된 상태일 때만 모임 탈퇴 메소드 호출
            if(joinStatus.equals("Y")) {

                clubService.leaveClub(requestDTO);

                redirectAttributes.addFlashAttribute("message", "모임 탈퇴가 완료되었습니다.");

            }

            return "redirect:/club/clubHome?clubNo=" + clubNo;

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "모임 탈퇴 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

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

        try {

            // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
            int master = clubService.checkClubMaster(clubNo);
            System.out.println("###### 모임장 no: " + master);

            // 모임장일 때만 모임 해체 메소드 호출
            if(memberNo == master) {

                List<AttachmentDTO> Attachment = new ArrayList<>();

                // 추후 해체에 성공했을 때 이미지 삭제를 위해 이미지 select
                try {

                    Attachment = clubService.selectClubImg(clubNo);

                } catch (Exception e) {

                    e.printStackTrace();

                    model.addAttribute("msg", "모임 해체 과정에서 문제가 발생했습니다.");

                    return "common/errorPage";

                }

                int result = clubService.deleteClub(requestDTO);

                // 모임 해체 성공 시 이미지 삭제
                if(result == 1) {

                    System.out.println("###### 삭제할 이미지: " + Attachment);

                    String savedName = Attachment.get(0).getSavedName();

                    String root = "/Users/hyeoni/Desktop/workspace/multiit/final_udonghaeng/udong/src/main/resources/static";
                    String filePath = root + "/uploadFiles";

                    new File(filePath + "/" + savedName).delete();

                }

                redirectAttributes.addFlashAttribute("message", "모임 해체가 완료되었습니다.");

                return "redirect:/club/clubMain?page=1";

            }

            return "redirect:/club/clubHome?clubNo=" + clubNo;

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "모임 해체 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

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


    @RequestMapping("/clubUpdateForm")
    public String clubUpdateForm(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        // 로그인된 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int clubNo = requestDTO.getClubNo();
        System.out.println("###### 수정할 모임 no: " + clubNo);

        try {

            // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
            int master = clubService.checkClubMaster(clubNo);
            System.out.println("###### 모임장 no: " + master);

            // 모임장일 때만 모임 수정폼으로 이동
            if(memberNo == master) {

                // service의 모임 홈 select 메소드 호출
                ClubDTO clubDTO = clubService.selectClubHome(requestDTO);

                System.out.println("###### 가져온 clubHome: " + clubDTO);

                model.addAttribute("clubHome", clubDTO);

                return "club/clubUpdateForm";

            }

            return "redirect:/club/clubHome?clubNo=" + clubNo;

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "모임 수정폼 이동 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    @PostMapping("/updateClub")
    public String updateClub(@AuthenticationPrincipal CustomUserDetails c, CategoryDTO categoryDTO, ClubDTO clubDTO, @RequestParam("img") MultipartFile img, Model model, RedirectAttributes redirectAttributes) {

        int clubNo = clubDTO.getClubNo();

        // 서버단에서 로그인된 유저가 모임장인지 한 번 더 검증
        int memberNo = c.getMemberDTO().getMemberNo();
        int master = 0;

        try {

            master = clubService.checkClubMaster(clubNo);

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "모임 수정 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

        // 모임장일 때만 모임 수정
        if(memberNo == master) {

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

                return "redirect:/club/clubHome?clubNo=" + clubNo;

            } catch (Exception e) {

                e.printStackTrace();

                model.addAttribute("msg", "모임 수정 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        return "redirect:/club/clubHome?clubNo=" + clubNo;

    }


    @RequestMapping("/clubLog/logMain")
    public String clubLog(@AuthenticationPrincipal CustomUserDetails c, FilterDTO filterDTO, Model model) {

        return "/club/clubLog/logMain";

    }


    @RequestMapping("/clubLog/logInsertForm")
    public void clubLogInsertForm() {

    }


}
