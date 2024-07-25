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
        // long membersLocation = c.getMemberDTO().getMemAddressDTO().getLocationCode();
        long membersLocation = 1117013100;
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
     * 우동 모임 생성폼으로 이동
     *
     * @since 2024 -07-23
     */
    @RequestMapping("clubInsertForm")
    public void clubInsertForm() {

    }


    /**
     * 모임 카테고리 리스트를 조회 (json, ajax)
     *
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    @RequestMapping("/categoryList")
    @ResponseBody // 요청한 곳으로 json 데이터 전달
    public List<CategoryDTO> categoryList() throws Exception {

        List<CategoryDTO> categoryList = clubService.selectCategoryList();
        System.out.println("###### 가져온 카테고리 리스트: " + categoryList);

        return categoryList;

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
        MasterDTO masterDTO = new MasterDTO();
        masterDTO.setMemberNo(memberNo);
        clubDTO.setMaster(masterDTO);

        // 로그인된 유저의 locationCode를 principal에서 받아와 모임 동네로 clubDTO에 set
        // long membersLocation = c.getMemberDTO().getMemAddressDTO().getLocationCode();
        LocationDTO locationDTO = new LocationDTO();
        // locationDTO.setLocationCode(mastersLocation);
        locationDTO.setLocationCode(1117013100);
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
            // return "redirect:/club/clubHome?clubNo="+clubNo;
            return "redirect:/club/clubMain?page=1";

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


}
