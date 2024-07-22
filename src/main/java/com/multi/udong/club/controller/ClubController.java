package com.multi.udong.club.controller;

import com.multi.udong.club.model.dto.CategoryDTO;
import com.multi.udong.club.model.dto.ClubDTO;
import com.multi.udong.club.model.dto.LocationDTO;
import com.multi.udong.club.model.dto.MasterDTO;
import com.multi.udong.club.service.ClubService;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/club")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    // 우동 모임 메인 페이지로 이동
    @RequestMapping("/clubMain")
    public String clubMain(Model model) {

        return "club/clubMain";

    }

    // 우동 모임 생성폼으로 이동
    @RequestMapping("clubInsertForm")
    public void clubInsertForm() {

    }

    // 모임 카테고리 리스트를 조회 (json, ajax)
    @RequestMapping("/categoryList")
    @ResponseBody // 요청한 곳으로 json 데이터 전달
    public List<CategoryDTO> categoryList() throws Exception {

        List<CategoryDTO> categoryList = clubService.selectCategoryList();

        System.out.println("가져온 카테고리 리스트는 >>>> " + categoryList);

        return categoryList;

    }

    // 모임 생성
    @PostMapping("/insertClub")
    public String insertClub(@AuthenticationPrincipal CustomUserDetails c, CategoryDTO categoryDTO, ClubDTO clubDTO, @RequestParam("img") MultipartFile img, HttpServletRequest request, Model model) {

        // 로그인된 유저의 no를 principal에서 받아와 모임 생성자(master)로 clubDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        MasterDTO masterDTO = new MasterDTO();
        masterDTO.setMemberNo(memberNo);
        clubDTO.setMaster(masterDTO);

        // 로그인된 유저의 locationCode를 principal에서 받아와 모임 동네로 clubDTO에 set
        // long mastersLocation = c.getMemberDTO().getLocationCode();
        LocationDTO locationDTO = new LocationDTO();
        //locationDTO.setLocationCode(mastersLocation);
        locationDTO.setLocationCode(1117013100);
        clubDTO.setLocation(locationDTO);

        // insertForm에서 받아온 카테고리 코드를 clubDTO에 set
        clubDTO.setCategory(categoryDTO);

        System.out.println("###### insert할 모임 데이터: " + clubDTO);

        // 이미지 저장할 경로 설정
        HttpSession session = request.getSession();
        String root = session.getServletContext().getRealPath("resources");

        System.out.println("###### resources 경로: " + root);

        String filePath = root + "/static/uploadFiles";

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

            // 그 attachmentDTO를 clubDTO에 set
            clubDTO.setAttachment(attachmentDTO);

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

            // service의 모임 insert 메소드 호출
            clubService.insertClub(clubDTO);

            // mapper의 useGeneratedKeys="true" keyProperty="clubNo" 때문에 clubDTO에 insert한 모임의 No(PK)가 담김
            int clubNo = clubDTO.getClubNo();

            // return "redirect:/club/clubDetail?clubNo="+clubNo;
            return "redirect:/club/clubMain";

        } catch (Exception e) {

            e.printStackTrace();

            // 오류 발생 시 생성한 파일을 삭제
            new File(filePath + "/" + savedName).delete();

            model.addAttribute("msg", "모임 생성 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }


    }


}
