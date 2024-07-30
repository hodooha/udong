package com.multi.udong.common;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The type Error controller.
 *
 * @author 김재식
 * @since 2024 -07-30
 */
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    /**
     * Handle error string.
     *
     * @param request the request
     * @param model   the model
     * @return the string
     * @since 2024 -07-30
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("msg", HttpStatus.valueOf(statusCode).getReasonPhrase());
        } else {
            model.addAttribute("msg", "알 수 없는 오류가 발생했습니다.");
        }

        return "common/errorPage";
    }

    /**
     * Get error path string.
     *
     * @return the string
     * @since 2024 -07-30
     */
    public String getErrorPath() {
        return "/error";
    }
}
