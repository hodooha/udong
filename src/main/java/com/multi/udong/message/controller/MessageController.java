package com.multi.udong.message.controller;

import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dto.MessageDTO;
import com.multi.udong.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
     * @param c              the c
     * @param page           the page
     * @param searchCategory the search category
     * @param searchWord     the search word
     * @param model          the model
     * @since 2024 -08-06
     */
    @GetMapping("/receivedMessage")
    public void getReceivedMessages(@AuthenticationPrincipal CustomUserDetails c,
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

        List<MessageDTO> data = messageService.getReceivedMessages(pageDTO);

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
    }

    /**
     * Get sent messages string.
     *
     * @param c              the c
     * @param page           the page
     * @param searchCategory the search category
     * @param searchWord     the search word
     * @param model          the model
     * @since 2024 -08-07
     */
    @GetMapping("/sentMessage")
    public void getSentMessages(@AuthenticationPrincipal CustomUserDetails c,
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

        List<MessageDTO> data = messageService.getSentMessages(pageDTO);

        if (!data.isEmpty()) {
            count = data.get(0).getTotalCount();
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;
        }

        List<String> header = Arrays.asList("받은 사람", "내용", "보낸 날짜", "상태");
        List<String> searchCategories = Arrays.asList("받은 사람", "내용");

        model.addAttribute("tableHeader", header);
        model.addAttribute("tableData", data);
        model.addAttribute("pages", pages);
        model.addAttribute("page", page);
        model.addAttribute("searchCategories", searchCategories);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchWord", searchWord);
    }

    /**
     * Send message.
     *
     * @param receiverNo the receiver no
     * @param model      the model
     * @since 2024 -08-07
     */
    @GetMapping("/sendMessageForm")
    public void sendMessageForm(@RequestParam(value = "memberNo", required = false) Integer receiverNo,
                                Model model) {

        if (receiverNo != null) {
            String receiverNickname = messageService.getNicknameByMemberNo(receiverNo);
            model.addAttribute("receiverNickname", receiverNickname);
        }
    }

    /**
     * Send message.
     *
     * @param c          the c
     * @param messageDTO the message dto
     * @return the response entity
     * @since 2024 -08-07
     */
    @PostMapping("/sendMessage")
    @ResponseBody
    public ResponseEntity<String> sendMessage(@AuthenticationPrincipal CustomUserDetails c,
                                              @RequestBody MessageDTO messageDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        messageDTO.setSenderNo(memberNo);
        messageService.sendMessage(messageDTO);

        return ResponseEntity.ok("쪽지를 보냈습니다.");
    }

    @PostMapping("/deleteMessages")
    @ResponseBody
    public Map<String, Boolean> deleteMessages(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> messageNos = request.get("messageNos");

        Map<String, Boolean> result = new HashMap<>();
        result.put("success", messageService.deleteMessages(messageNos));
        return result;
    }
}
