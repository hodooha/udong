package com.multi.udong.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The type Main controller.
 *
 * @since 2024 -07-21
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    /**
     * Index string.
     *
     * @return the string
     * @since 2024 -07-21
     */
    @RequestMapping("/")
    public String index(){
        return "index";
    }
    
}
