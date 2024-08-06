package com.multi.udong.message.controller;

import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dto.MessageDTO;
import com.multi.udong.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

/**
 * The type Message controller.
 *
 * @author 김재식
 * @since 2024 -08-06
 */
@RequestMapping("/message")
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * Message main string.
     *
     * @param c     the c
     * @param page  the page
     * @param model the model
     * @return the string
     * @since 2024 -08-06
     */
    @GetMapping("/receiveMessage")
    public String selectReceivedMessages(@AuthenticationPrincipal CustomUserDetails c,
                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "searchCategory", required = false) String searchCategory,
                                         @RequestParam(value = "searchWord", required = false) String searchWord,
                                         Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int count;
        int pages = 1;

        PageDTO pageDTO = new PageDTO();
        pageDTO.setMemberNo(memberNo);
        pageDTO.setPage(page);
        pageDTO.setStartEnd(page);
        pageDTO.setSearchCategory(searchCategory);
        pageDTO.setSearchWord(searchWord);

        List<MessageDTO> data = messageService.selectReceivedMessages(pageDTO);

        if (!data.isEmpty()) {
            count = data.get(0).getTotalCount();
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;
        }

        List<String> header = Arrays.asList("보낸 사람", "내용", "받은 날짜", "상태");
        List<String> searchCategories = Arrays.asList("보낸 사람", "내용");

        model.addAttribute("tableHeader", header);
        model.addAttribute("tableData", data);
        model.addAttribute("pages", pages);
        model.addAttribute("page", page);
        model.addAttribute("searchCategories", searchCategories);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchWord", searchWord);
        
        return "message/receiveMessage";
    }
}
