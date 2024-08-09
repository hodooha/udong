package com.multi.udong.share.controller;


import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.service.MemberService;
import com.multi.udong.share.model.dto.*;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Sha dream controller.
 *
 * @author 하지은
 * @since 2024 -08-09
 */
@RequiredArgsConstructor
@RequestMapping("/share/dream")
@Controller
public class ShaDreamController {

    private final ShareService shareService;
    private final MemberService memberService;

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
            return "common/errorPage";
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
            return "common/errorPage";
        }
        model.addAttribute("catList", catList);
        return "share/dreamBorrow";
    }

    /**
     * 빌려드림 목록 조회
     *
     * @param criteriaDTO the criteria dto
     * @param c           the c
     * @param model       the model
     * @return the lend list
     */
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

    /**
     * 요청드림 목록 조회
     *
     * @param criteriaDTO the criteria dto
     * @param c           the c
     * @param model       the model
     * @return the borrow list
     */
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

    /**
     * 물건의 대여 요청자 조회
     *
     * @param reqDTO the req dto
     * @param c      the c
     * @param model  the model
     * @return the requesters
     */
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

    /**
     * 빌려드림 내 대여확정 처리
     *
     * @param reqDTO the req dto
     * @param c      the c
     * @param model  the model
     * @return the string
     * @since 2024 -08-09
     */
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

    /**
     * 해당 물건의 대여중인 요청 건 조회
     *
     * @param reqDTO the req dto
     * @param model  the model
     * @return the rented req
     */
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

    /**
     * 빌려드림 목록 내 대여인의 차용인 평가 및 반납완료 처리
     *
     * @param evalDTO the eval dto
     * @param c       the c
     * @param model   the model
     * @return the string
     * @since 2024 -08-06
     */
    @PostMapping("/evalWithReturnReq")
    public String evalWithReturnReq(ShaEvalDTO evalDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {
        System.out.println(evalDTO);
        try {
            shareService.evalWithReturnReq(evalDTO, c);

            // 대여인 점수 & 레벨 변경 후 사용자 세션 최신화
            memberService.updateMemberSession();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }

        return "share/dreamLend";
    }

    /**
     * 요청드림 내 대여 및 나눔 요청 취소
     *
     * @param shaReqDTO the sha req dto
     * @param c         the c
     * @param model     the model
     * @return the string
     * @since 2024 -08-06
     */
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

    /**
     * 요청드림 내 차용인의 대여인 평가 및 평가완료 처리
     *
     * @param evalDTO the eval dto
     * @param c       the c
     * @param model   the model
     * @return the string
     * @since 2024 -08-06
     */
    @PostMapping("/evalWithEndReq")
    public String evalWithEndReq(ShaEvalDTO evalDTO, @AuthenticationPrincipal CustomUserDetails c, Model model) {

        System.out.println(evalDTO);
        try {
            shareService.evalWithEndReq(evalDTO, c);

            // 차용인 점수 & 레벨 변경 후 사용자 세션 최신화
            memberService.updateMemberSession();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
        }

        return "share/dreamBorrow";


    }

    /**
     * 신고 등록
     *
     * @param itemNo    the item no
     * @param reportDTO the report dto
     * @param c         the c
     * @return the string
     * @since 2024 -08-09
     */
    @PostMapping("/insertReport")
    @ResponseBody
    public String insertReport(@RequestParam("itemNo") int itemNo, ShaReportDTO reportDTO, @AuthenticationPrincipal CustomUserDetails c) {

        reportDTO.reasonConcat();
        String itemGroup = reportDTO.getTypeCode();
        reportDTO.setUrl("/share/" + itemGroup + "/detail?itemNo=" + itemNo);
        reportDTO.setTypeCode(itemGroup.toUpperCase());

        System.out.println(reportDTO);

        String msg = "신고가 접수되었습니다.";

        try {
            shareService.insertReport(reportDTO, c);

        } catch (Exception e) {
            e.printStackTrace();
            msg = e.getMessage();
        }

        return msg;
    }



}
