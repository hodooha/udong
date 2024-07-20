package com.multi.udong.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class maincontroller {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/myPage")
    public String myPage(){
        return "member/myPage";
    }
}
