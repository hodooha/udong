package com.multi.udong.share.controller;


import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dto.ShaDreamCriteriaDTO;
import com.multi.udong.share.model.dto.ShaDreamPageDTO;
import com.multi.udong.share.model.dto.ShaDreamResultDTO;
import com.multi.udong.share.model.dto.ShaReqDTO;
import com.multi.udong.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

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
    public String dreamLendMain(Model model, @AuthenticationPrincipal CustomUserDetails c){
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
    public String dreamBorrowMain(Model model, @AuthenticationPrincipal CustomUserDetails c){
        return "share/dreamBorrow";
    }

    @GetMapping("/lendList")
    @ResponseBody
    public ResponseEntity<?> getLendList(ShaDreamCriteriaDTO criteriaDTO, @AuthenticationPrincipal CustomUserDetails c){

        // 결과값 초기 설정
        Map<String, Object> result = new HashMap<>();
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
            result.put("lendList", resultDTO.getLendList());
            result.put("pageInfo", pageInfo);

            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }

    }

    @GetMapping("/borrowList")
    @ResponseBody
    public ResponseEntity<?> getBorrowList(ShaReqDTO reqDTO, @AuthenticationPrincipal CustomUserDetails c){

        System.out.println(reqDTO);

//        if(reqDTO.getReqGroup().equals("lend")){
//            reqDTO.setOwnerNo(c.getMemberDTO().getMemberNo());
//        }else {
//            reqDTO.setRqstNo(c.getMemberDTO().getMemberNo());
//        }
//
//        try {
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }



        return null;
    }
}
