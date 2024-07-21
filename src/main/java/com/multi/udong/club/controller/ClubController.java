package com.multi.udong.club.controller;

import com.multi.udong.club.model.dto.CategoryDTO;
import com.multi.udong.club.service.ClubService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

}
