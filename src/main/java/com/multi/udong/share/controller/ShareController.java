package com.multi.udong.share.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.security.CustomUserDetails;
import com.multi.udong.share.model.dto.*;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 대여 및 나눔 Controller
 *
 * @author 하지은
 * @since 2024 -07-21
 */
@RequiredArgsConstructor
@RequestMapping("/share")
@Controller
public class ShareController {

    private final ShareService shareService;


    /**
     * 대여 메인페이지 이동
     *
     * @return the string
     * @since 2024 -07-26
     */
    @GetMapping("/rent")
    public String rentMain() {
        return "share/rentMain";
    }


    /**
     * 나눔 메인페이지 이동
     *
     * @return the string
     * @since 2024 -07-26
     */
    @GetMapping("/give")
    public String giveMain() {
        return "share/giveMain";
    }

    /**
     * 카테고리 목록 조회
     *
     * @return the list
     * @since 2024 -07-26
     */
    @GetMapping("/getCatList")
    @ResponseBody
    public List<ShaCatDTO> getCatList() {
        List<ShaCatDTO> catList = null;
        try {
            catList = shareService.getShaCat();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return catList;
    }

    /**
     * 물건 등록 페이지 이동 및 카테고리 목록 조회
     *
     * @param model the model
     * @param c     the c
     * @return the string
     * @since 2024 -07-22
     */
    @GetMapping("/register")
    public String register(Model model, @AuthenticationPrincipal CustomUserDetails c) {

        try {
            // 로그인 확인
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }

            // 지역 등록 여부 확인
            long locCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();
            if (locCode == 0) {
                throw new Exception("지역을 먼저 등록해주세요.");
            }

            // db에서 카테고리 목록 조회
            List<ShaCatDTO> list = shareService.getShaCat();
            model.addAttribute("catList", list);
            return "share/registerForm";
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }
    }

    /**
     * 물건 상세 조회 페이지 이동 및 상세 조회
     *
     * @param itemDTO the item dto
     * @param model   the model
     * @param c       the c
     * @return the string
     * @since 2024 -07-23
     */
    @GetMapping(value = {"/rent/detail", "/give/detail"})
    public String itemDetail(ShaItemDTO itemDTO, Model model, @AuthenticationPrincipal CustomUserDetails c) {

        try {
            // 로그인 확인
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }

            // 지역 등록 여부 확인
            long locCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();
            if (locCode == 0) {
                throw new Exception("지역을 먼저 등록해주세요.");
            }

            // db에서 물건 상세 정보 조회
            ShaItemDTO item = shareService.getItemDetail(itemDTO);
            model.addAttribute("item", item);

        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }


        return "share/itemDetail";
    }

    /**
     * 물건 등록 및 첨부파일 저장 기능
     *
     * @param model    the model
     * @param itemDTO  the item dto
     * @param c        the principal
     * @param fileList the file list
     * @return the string
     * @since 2024 -07-22
     */
    @PostMapping("/register")
    public String insertItem(Model model, ShaItemDTO itemDTO, @AuthenticationPrincipal CustomUserDetails c, @RequestPart(name = "imgs") List<MultipartFile> fileList) {

        // 첨부파일 목록 정리 (데이터가 있는 것만!)
        fileList = fileList.stream().filter((x) -> !x.isEmpty()).collect(Collectors.toList());
        List<AttachmentDTO> imgList = new ArrayList<>();

        try {

            // 로그인 확인
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }

            // 지역 등록 여부 확인
            long locCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();
            if (locCode == 0) {
                throw new Exception("지역을 먼저 등록해주세요.");
            }

            // 물건 정보 설정
            itemDTO.setLocCode(c.getMemberDTO().getMemAddressDTO().getLocationCode());
            itemDTO.setOwnerNo(c.getMemberDTO().getMemberNo());

            // 이미지 저장 경로
            String imgPath = "C:\\Users\\user\\uploadFiles";

            // 첨부파일 있을 경우
            if (!fileList.isEmpty()) {

                // 이미지 저장 폴더 만들기
                File mkdir = new File(imgPath);
                if (!mkdir.exists()) {
                    mkdir.mkdirs();
                }

                // db에 저장할 첨부파일 정보 설정 및 파일 저장
                for (MultipartFile f : fileList) {

                    String originalFileName = f.getOriginalFilename();
                    String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                    AttachmentDTO img = new AttachmentDTO();
                    img.setOriginalName(originalFileName);
                    img.setSavedName(savedName);
                    img.setTypeCode(itemDTO.getItemGroup());

                    imgList.add(img);
                    f.transferTo(new File(imgPath + "\\" + savedName));

                }
            }

            // db에 물건 정보 저장
            int result = shareService.insertItem(itemDTO, imgList);

            // 물건 정보 저장 실패 시 저장한 첨부파일 삭제
            if (result < 1) {
                for (AttachmentDTO img : imgList) {
                    new File(imgPath + "\\" + img.getSavedName()).delete();
                }
                throw new Exception();
            }

        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }

        return itemDTO.getItemGroup().equals("rent") ? "redirect:/share/rent" : "redirect:/share/give";
    }


    /**
     * 물건 검색 & 물건 목록 조회
     *
     * @param criteriaDTO the criteria dto
     * @param c           the c
     * @return the list
     * @since 2024 -07-24
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<?> searchItems(ShaCriteriaDTO criteriaDTO, @AuthenticationPrincipal CustomUserDetails c) {

        // 결과값 초기 설정
        Map<String, Object> result = new HashMap<>();
        ShaPageDTO pageInfo = new ShaPageDTO();
        List<ShaItemDTO> itemList = null;

        System.out.println("여기!!!!!" + criteriaDTO);

        try {

            // 로그인 확인
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }

            // 지역 등록 여부 확인
            long locCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();
            if (locCode == 0) {
                throw new Exception("지역을 먼저 등록해주세요.");
            }

            // 검색 조건에 로그인한 유저의 지역 code 설정
            criteriaDTO.setLocCode(locCode);

            // 검색 조건에 페이지에 보여지는 물건 번호 범위 설정
            criteriaDTO.setPageRange(criteriaDTO.getPage());

            // 검색 조건에 맞는 물건 목록 가져오기
            itemList = shareService.searchItems(criteriaDTO);

            // 검색 조건에 맞는 물건의 총 개수 확인
            int totalCounts = shareService.getItemCounts(criteriaDTO);

            // 클라이언트에 보여지는 페이지네이션 정보 설정
            pageInfo.setCurrentPage(criteriaDTO.getPage());
            pageInfo.setPageInfo(totalCounts);

            System.out.println(itemList);

            // 결과값에 물건 목록과 페이지네이션 정보 저장
            result.put("itemList", itemList);
            result.put("pageInfo", pageInfo);

            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            result.put("msg", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 대여 및 나눔 신청
     *
     * @param reqDTO the req dto
     * @param c      the c
     * @return the string
     * @since 2024 -07-28
     */
    @PostMapping("/request")
    @ResponseBody
    public String insertRequest(ShaReqDTO reqDTO, @AuthenticationPrincipal CustomUserDetails c) {

        // 결과 메세지 설정
        String reqGroup = reqDTO.getReqGroup().equals("rent") ? "대여" : "나눔";
        String msg = reqGroup + "신청 성공";

        try {
            // 로그인 확인
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }

            // 지역 등록 여부 확인
            long locCode = c.getMemberDTO().getMemAddressDTO().getLocationCode();
            if (locCode == 0) {
                throw new Exception("지역을 먼저 등록해주세요.");
            }

            // 신청자와 물건의 소유자 동일 여부 확인
            if(c.getMemberDTO().getMemberNo() == reqDTO.getOwnerNo()){
                throw new Exception("본인의 물건은 대여 및 나눔 신청할 수 없습니다.");
            }

            System.out.println(shareService.findRequest(reqDTO));

            // 신청자 no에 로그인한 유저 no 설정
            reqDTO.setRqstNo(c.getMemberDTO().getMemberNo());

            // 동일 물건에 대한 대여 및 나눔 요청 유무 확인
            if(shareService.findRequest(reqDTO) != null){
                throw new Exception("이미 신청하셨습니다.");
            }
            
            System.out.println(reqDTO);
            
            // db에 대여 및 나눔 신청 저장
            int result = shareService.insertRequest(reqDTO);
            if(result < 0) {
                throw new Exception(reqGroup + "신청 실패");
            }

        } catch (Exception e) {
            msg = e.getMessage();
            e.printStackTrace();
        }
        
        return msg;
    }

}
