package com.multi.udong.admin.controller;

import com.multi.udong.admin.service.AdminService;
import org.apache.ibatis.javassist.compiler.ast.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/adminMain")
    public String adminMain(Model model) {
        model.addAttribute("members", adminService.getAllMembers());
        return "admin/adminMain";
    }
    @GetMapping("/memberSearch")
    public String searchMembers(@RequestParam(value = "search", required = false) String search, Model model) {
        if (search == null || search.trim().isEmpty()) {
            // 검색어가 없을 경우 기본 화면으로 리디렉션
            return "redirect:/admin/adminMain";
        }

        List<Member> members = adminService.searchMembersByIdOrNickname(search);
        model.addAttribute("members", members);
        return "admin/adminMain";
    }
}