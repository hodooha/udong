package com.multi.udong.share.controller;


import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dto.*;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/share/dream")
@Controller
public class ShaDreamController {

    private final ShareService shareService;

    /**
     * 빌려(나눠)드림 페이지 이동
     *
     * @param model the model
     * @param c     the c
     * @return the string
     * @since 2024 -08-02
     */
    @GetMapping("/lend")
    public String dreamLendMain(Model model, @AuthenticationPrincipal CustomUserDetails c) {
        List<ShaCatDTO> catList = null;
        try {
            // 로그인 확인
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }
            catList = shareService.getShaCat();
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }
        model.addAttribute("catList", catList);
        return "share/dreamLend";
    }

    /**
     * 요청드림 페이지 이동
     *
     * @param model the model
     * @param c     the c
     * @return the string
     * @since 2024 -08-02
     */
    @GetMapping("/borrow")
    public String dreamBorrowMain(Model model, @AuthenticationPrincipal CustomUserDetails c) {
        List<ShaCatDTO> catList = null;
        try {
            // 로그인 확인
            if (c == null) {
                throw new Exception("로그인을 먼저 해주세요.");
            }
            catList = shareService.getShaCat();
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }
        model.addAttribute("catList", catList);
        return "share/dreamBorrow";
    }

    @GetMapping("/lendList")
    public String getLendList(ShaDreamCriteriaDTO criteriaDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        // 결과값 초기 설정
        ShaDreamPageDTO pageInfo = new ShaDreamPageDTO();

        criteriaDTO.setOwnerNo(c.getMemberDTO().getMemberNo());
        criteriaDTO.setPageRange(criteriaDTO.getPage());

        try {
            // 빌려(나눠)드림 목록 조회
            ShaDreamResultDTO resultDTO = shareService.getLendList(criteriaDTO);

            // 클라이언트에 보여지는 페이지네이션 정보 설정
            pageInfo.setCurrentPage(criteriaDTO.getPage());
            pageInfo.setPageInfo(resultDTO.getTotalCounts());

            // 결과값에 물건 목록과 페이지네이션 정보 저장
            model.addAttribute("lendList", resultDTO.getLendList());
            model.addAttribute("pageInfo", pageInfo);


        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }

        return "share/dreamLend :: #dreams";

    }

    @GetMapping("/borrowList")
    public String getBorrowList(ShaDreamCriteriaDTO criteriaDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        // 결과값 초기 설정
        ShaDreamPageDTO pageInfo = new ShaDreamPageDTO();

        criteriaDTO.setRqstNo(c.getMemberDTO().getMemberNo());
        criteriaDTO.setPageRange(criteriaDTO.getPage());

        try {
            // 요청드림 목록 조회
            ShaDreamResultDTO resultDTO = shareService.getBorrowList(criteriaDTO);

            System.out.println(resultDTO);

            // 클라이언트에 보여지는 페이지네이션 정보 설정
            pageInfo.setCurrentPage(criteriaDTO.getPage());
            pageInfo.setPageInfo(resultDTO.getTotalCounts());

            // 결과값에 물건 목록과 페이지네이션 정보 저장
            model.addAttribute("borrowList", resultDTO.getBorrowList());
            model.addAttribute("pageInfo", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }


        return "share/dreamBorrow :: #reqDreams";
    }

    @GetMapping("/requesters")
    public String getRequesters(ShaReqDTO reqDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        try {
            List<ShaReqDTO> requesters = shareService.getRequesters(reqDTO);
            model.addAttribute("requesters", requesters);
            System.out.println(requesters);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }


        return "share/dreamLend :: #dreamModals";
    }

    @PostMapping("/approveReq")
    public String approveReq(ShaReqDTO reqDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        System.out.println("넘어온 확정 req" + reqDTO);
        try {
            shareService.approveReq(reqDTO);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }


        return "share/dreamLend";
    }

    @GetMapping("/getRentedReq")
    public String getRentedReq(ShaReqDTO reqDTO, Model model) {

        try {
            ShaReqDTO result = shareService.findRequest(reqDTO);
            System.out.println(result);
            model.addAttribute("req", result);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }

        return "share/dreamLend :: #evalAndReportModal";
    }

    @PostMapping("/evalWithReturnReq")
    public String evalWithReturnReq(ShaEvalDTO evalDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {
        System.out.println(evalDTO);
        try {
            shareService.evalWithReturnReq(evalDTO, c);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }

        return "share/dreamLend";
    }

    @GetMapping("/deleteReq")
    public String deleteReq(ShaReqDTO shaReqDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        System.out.println("삭제 대상 req" + shaReqDTO);
        try {
            shareService.deleteReq(shaReqDTO, c);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }

        return "share/dreamBorrow";
    }

    @PostMapping("/evalWithEndReq")
    public String evalWithEndReq(ShaEvalDTO evalDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        System.out.println(evalDTO);
        try {
            shareService.evalWithEndReq(evalDTO, c);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }

        return "share/dreamBorrow";


    }

    @PostMapping("/insertReport")
    @ResponseBody
    public String insertReport(@RequestParam("itemNo") int itemNo, ShaReportDTO reportDTO, @AuthenticationPrincipal CustomUserDetails c) {

        reportDTO.reasonConcat();
        String itemGroup = reportDTO.getTypeCode();
        reportDTO.setUrl("/share/" + itemGroup + "/detail?itemNo=" + itemNo);
        reportDTO.setTypeCode(itemGroup.toUpperCase());

        System.out.println(reportDTO);

        String msg = "신고가 접수되었습니다.";

//        try {
//            shareService.insertReport(reportDTO, c);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            msg = e.getMessage();
//        }

        return msg;
    }

}
