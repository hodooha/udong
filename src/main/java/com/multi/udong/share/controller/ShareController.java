package com.multi.udong.share.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dto.*;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
     * The constant IMAGE_PATH.
     */
// 이미지 저장 경로
    static final String IMAGE_PATH = "C:\\Users\\user\\uploadFiles";

    /**
     * The constant SEC.
     */
    public static final int SEC = 60;
    /**
     * The constant MIN.
     */
    public static final int MIN = 60;
    /**
     * The constant HOUR.
     */
    public static final int HOUR = 24;
    /**
     * The constant DAY.
     */
    public static final int DAY = 30;
    /**
     * The constant MONTH.
     */
    public static final int MONTH = 12;

    /**
     * 대여 메인페이지 이동
     *
     * @param model the model
     * @param c     the c
     * @return the string
     * @since 2024 -07-26
     */
    @GetMapping("/rent")
    public String rentMain(Model model, @AuthenticationPrincipal CustomUserDetails c) {
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
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }
        return "share/rentMain";
    }


    /**
     * 나눔 메인페이지 이동
     *
     * @param model the model
     * @param c     the c
     * @return the string
     * @since 2024 -07-26
     */
    @GetMapping("/give")
    public String giveMain(Model model, @AuthenticationPrincipal CustomUserDetails c) {
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
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }
        return "share/giveMain";
    }


    /**
     * 카테고리 목록 조회
     *
     * @return the list
     * @since 2024 -07-26
     */
    @GetMapping("/getCatList")
    public String getCatList(Model model) {
        try {
            List<ShaCatDTO> catList = shareService.getShaCat();
            model.addAttribute("catList", catList);
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            return "common/errorPage";
        }

        return "share/rentMain :: #catSelect";
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
     * 물건 상세 조회 시 보여지는 날짜 설정 메소드
     *
     * @param localDateTime the local date time
     * @return the string
     * @since 2024 -08-01
     */
    public static String convertLocaldatetimeToTime(LocalDateTime localDateTime) {

        LocalDateTime now = LocalDateTime.now();

        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        String displayDate = null;
        if (diffTime < SEC){
            return diffTime + "초전";
        }
        diffTime = diffTime / SEC;
        if (diffTime < MIN) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / MIN;
        if (diffTime < HOUR) {
            return diffTime + "시간 전";
        }
        diffTime = diffTime / HOUR;
        if (diffTime < DAY) {
            return diffTime + "일 전";
        }
        diffTime = diffTime / DAY;
        if (diffTime < MONTH) {
            return diffTime + "개월 전";
        }

        diffTime = diffTime / MONTH;
        return diffTime + "년 전";

    };

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
            ShaItemDTO item = shareService.getItemDetailWithViewCnt(itemDTO, c);
            model.addAttribute("item", item);

            // 물건 상세 조회 시 보여지는 날짜 설정
            model.addAttribute("displayDate", convertLocaldatetimeToTime(item.getModifiedAt()));


        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }


        return "share/itemDetail";
    }


    /**
     * 물건 상세페이지 업데이트
     *
     * @param itemDTO the item dto
     * @param model   the model
     * @param c       the c
     * @return the response entity
     * @since 2024 -08-02
     */
    @GetMapping(value = {"/rent/updateDetail", "/give/updateDetail"})
    public String updateDetail(ShaItemDTO itemDTO, Model model, @AuthenticationPrincipal CustomUserDetails c) {

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
            ShaItemDTO item = shareService.getItemDetail(itemDTO, c);
            model.addAttribute("item", item);

            // 물건 상세 조회 시 보여지는 날짜 설정
            model.addAttribute("displayDate", convertLocaldatetimeToTime(item.getModifiedAt()));

        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
        }

        return "share/itemDetail :: #detail";
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

            // 첨부파일 있을 경우
            if (!fileList.isEmpty()) {

                // 이미지 저장 폴더 만들기
                File mkdir = new File(IMAGE_PATH);
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
                    f.transferTo(new File(IMAGE_PATH + "\\" + savedName));

                }
            }

            // db에 물건 정보 저장
            shareService.insertItem(itemDTO, imgList);

        } catch (Exception e) {
            // 물건 정보 저장 실패 시 저장한 첨부파일 삭제
            for (AttachmentDTO img : imgList) {
                new File(IMAGE_PATH + "\\" + img.getSavedName()).delete();
            }
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
    public String searchItems(ShaCriteriaDTO criteriaDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        // 결과값 초기 설정
        ShaPageDTO pageInfo = new ShaPageDTO();

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

            // 검색 조건에 맞는 물건 목록, totalCounts 가져오기
            ShaItemResultDTO searchResult = shareService.searchItems(criteriaDTO);

            // 클라이언트에 보여지는 페이지네이션 정보 설정
            pageInfo.setCurrentPage(criteriaDTO.getPage());
            pageInfo.setPageInfo(searchResult.getTotalCounts());

            // 결과값에 물건 목록과 페이지네이션 정보 저장
            model.addAttribute("itemList", searchResult.getItemList());
            model.addAttribute("pageInfo", pageInfo);

            System.out.println(pageInfo);
            return "share/rentMain :: #itemList";

        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            e.printStackTrace();
            return "common/errorPage";
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
            if (c.getMemberDTO().getMemberNo() == reqDTO.getOwnerNo()) {
                throw new Exception("본인의 물건은 대여 및 나눔 신청할 수 없습니다.");
            }

            // 신청자 no에 로그인한 유저 no 설정
            reqDTO.setRqstNo(c.getMemberDTO().getMemberNo());

            // db에 대여 및 나눔 신청 저장
            shareService.insertRequest(reqDTO);


        } catch (Exception e) {
            msg = e.getMessage();
            e.printStackTrace();
        }

        return msg;
    }


    /**
     * 물건 수정 페이지 이동
     *
     * @param itemDTO the item dto
     * @param model   the model
     * @param c       the c
     * @return the string
     * @since 2024 -07-29
     */
    @GetMapping("/update")
    public String editForm(ShaItemDTO itemDTO, Model model, @AuthenticationPrincipal CustomUserDetails c) {

        System.out.println(itemDTO);

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

            // 물건 정보 불러오기
            ShaItemDTO target = shareService.getItemDetail(itemDTO, c);
            model.addAttribute("item", target);

            // 물건 소유자가 아닐 경우 예외 던지기
            if (target.getOwnerNo() != c.getMemberDTO().getMemberNo()) {
                throw new Exception("수정 권한이 없습니다.");
            }

            // db에서 카테고리 목록 조회
            List<ShaCatDTO> list = shareService.getShaCat();
            model.addAttribute("catList", list);


            return "share/editForm";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
            return "common/error";
        }
    }

    /**
     * 물건 정보 수정 (파일 포함)
     *
     * @param itemDTO  the item dto
     * @param model    the model
     * @param c        the c
     * @param fileList the file list
     * @return the string
     * @since 2024 -07-30
     */
    @PostMapping("/update")
    public String updateItem(ShaItemDTO itemDTO, Model model, @AuthenticationPrincipal CustomUserDetails c, @RequestPart(name = "imgs") List<MultipartFile> fileList) {

        System.out.println("수정하려는 정보: " + itemDTO);

        // 리턴 페이지 주소
        String page = "redirect:/share/" + itemDTO.getItemGroup() + "/detail?itemNo=" + itemDTO.getItemNo();

        // 첨부파일 목록 정리 (데이터가 있는 것만!)
        fileList = fileList.stream().filter((x) -> !x.isEmpty()).collect(Collectors.toList());
        List<AttachmentDTO> newImgList = new ArrayList<>();
        System.out.println(fileList);

        try {

            // 물건 소유자가 아닐 경우 예외 던지기
            if (itemDTO.getOwnerNo() != c.getMemberDTO().getMemberNo()) {
                throw new Exception("수정 권한이 없습니다.");
            }

            // 물건 등록 지역과 현재 유저의 지역 동일 여부 확인
            if (!Objects.equals(itemDTO.getLocCode(), c.getMemberDTO().getMemAddressDTO().getLocationCode())) {
                throw new Exception("지역이 다릅니다. 지역을 확인해주세요.");
            }

            // 삭제될 파일 처리
            List<AttachmentDTO> delImgList = new ArrayList<>();
            List<String> delFilesNo = itemDTO.getDelFilesNo();
            List<String> delFilesName = itemDTO.getDelFilesName();
            delFilesNo = delFilesNo.stream().filter((x) -> !x.isEmpty()).toList();
            delFilesName = delFilesName.stream().filter((x) -> !x.isEmpty()).toList();

            for (String f : delFilesNo) {
                AttachmentDTO delImg = new AttachmentDTO();
                delImg.setFileNo(Integer.parseInt(f));
                delImgList.add(delImg);
            }

            // 새로운 첨부파일 있을 경우

            if (!fileList.isEmpty()) {

                // 이미지 저장 폴더 만들기
                File mkdir = new File(IMAGE_PATH);
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
                    img.setTargetNo(itemDTO.getItemNo());

                    newImgList.add(img);
                    f.transferTo(new File(IMAGE_PATH + "\\" + savedName));

                }
            }

            // db에 물건 정보 저장
            shareService.updateItem(itemDTO, newImgList, delImgList);

            // 물건 정보 수정 성공 시 유저가 삭제한 파일 로컬에서 삭제
            for (String f : delFilesName) {
                new File(IMAGE_PATH + "\\" + f).delete();
            }

        } catch (Exception e) {
            for (AttachmentDTO img : newImgList) {
                new File(IMAGE_PATH + "\\" + img.getSavedName()).delete();
            }
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());

            return "common/errorPage";
        }


        return page;
    }

    /**
     * 물건 삭제
     *
     * @param itemDTO the item dto
     * @param c       the c
     * @param model   the model
     * @return the string
     * @since 2024 -07-31
     */
    @GetMapping("/delete")
    public String deleteItem(ShaItemDTO itemDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {
        System.out.println("삭제할 대상: " + itemDTO);

        try {
            // 물건 삭제
            List<AttachmentDTO> delImgs = shareService.deleteItem(itemDTO, c);

            // 물건 삭제 성공 시 삭제한 파일 로컬에서 삭제
            for (AttachmentDTO img : delImgs) {
                new File(IMAGE_PATH + "\\" + img.getSavedName()).delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
            return "common/errorPage";
        }

        return itemDTO.getItemGroup().equals("rent") ? "redirect:/share/rent" : "redirect:/share/give";
    }


    /**
     * 물건 상태 변경
     *
     * @param itemDTO the item dto
     * @param c       the c
     * @param model   the model
     * @return the string
     * @since 2024 -08-01
     */
    @GetMapping("/updateItStat")
    @ResponseBody
    public String updateItStat(ShaItemDTO itemDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        System.out.println(itemDTO);
        // 리턴 페이지 주소
        String msg = "상태 변경 완료";

        try {
            // 물건 상태 업데이트
            shareService.updateItStat(itemDTO, c);


        } catch (Exception e) {
            e.printStackTrace();
            msg = e.getMessage();

        }

        return msg;
    }

    /**
     * 찜 등록 및 삭제
     *
     * @param likeDTO the like dto
     * @param c       the c
     * @return the string
     * @since 2024 -08-01
     */
    @GetMapping("/like")
    @ResponseBody
    public String updateShaLike(ShaLikeDTO likeDTO, @AuthenticationPrincipal CustomUserDetails c){
        // 결과 메세지 설정

        String msg = "찜 변경 성공!";

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

            // 신청자 no에 로그인한 유저 no 설정
            likeDTO.setMemberNo(c.getMemberDTO().getMemberNo());

            // db에 찜 내역 변경
            shareService.updateShaLike(likeDTO);


        } catch (Exception e) {
            msg = e.getMessage();
            e.printStackTrace();
        }

        return msg;
    }




}
