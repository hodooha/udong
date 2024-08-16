package com.multi.udong.common;

import com.multi.udong.admin.model.dto.NoticeDTO;
import com.multi.udong.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    private final NoticeService noticeService;
    @GetMapping("/")
    public String index(Model model) {
        NoticeDTO popupNotice = noticeService.getActivePopupNoticeWithAttachment();
        if (popupNotice != null) {
            model.addAttribute("popupNotice", popupNotice);
        }
        return "index";
    }
    
}
