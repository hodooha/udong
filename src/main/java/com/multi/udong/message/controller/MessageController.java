package com.multi.udong.message.controller;

import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dto.MessageBlockDTO;
import com.multi.udong.message.model.dto.MessageDTO;
import com.multi.udong.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param messageNo      the message no
     * @param model          the model
     * @since 2024 -08-06
     */
    @GetMapping("/receivedMessage")
    public void getReceivedMessages(@AuthenticationPrincipal CustomUserDetails c,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "searchCategory", required = false) String searchCategory,
                                    @RequestParam(value = "searchWord", required = false) String searchWord,
                                    @RequestParam(value = "messageNo", required = false) Integer messageNo,
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

        // 상세 정보
        if (messageNo != null) {
            MessageDTO detail = messageService.getReceivedMessageDetail(messageNo);

            model.addAttribute("type", "receive");
            model.addAttribute("messageNo", messageNo);
            model.addAttribute("senderNo", detail.getSenderNo());
            model.addAttribute("isBlocked", messageService.getIsBlocked(detail));
            model.addAttribute("detail", detail);
        }

        List<String> header = Arrays.asList("보낸 사람", "내용", "받은 날짜", "상태");
        List<MessageDTO> data = messageService.getReceivedMessages(pageDTO);

        if (!data.isEmpty()) {
            count = data.get(0).getTotalCount();
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;
        }

        List<String> searchCategories = Arrays.asList("보낸 사람", "내용");

        // 테이블
        model.addAttribute("tableHeader", header);
        model.addAttribute("tableData", data);

        // 페이지
        model.addAttribute("pages", pages);
        model.addAttribute("page", page);

        // 검색 결과
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
     * @param messageNo      the message no
     * @param model          the model
     * @since 2024 -08-07
     */
    @GetMapping("/sentMessage")
    public void getSentMessages(@AuthenticationPrincipal CustomUserDetails c,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "searchCategory", required = false) String searchCategory,
                                @RequestParam(value = "searchWord", required = false) String searchWord,
                                @RequestParam(value = "messageNo", required = false) Integer messageNo,
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

        // 상세 정보
        if (messageNo != null) {
            MessageDTO detail = messageService.getSentMessageDetail(messageNo);

            model.addAttribute("type", "send");
            model.addAttribute("messageNo", messageNo);
            model.addAttribute("receiverNo", detail.getReceiverNo());
            model.addAttribute("isBlocked", messageService.getIsBlocked(detail));
            model.addAttribute("detail", detail);
        }

        List<String> header = Arrays.asList("받은 사람", "내용", "보낸 날짜", "상태");
        List<MessageDTO> data = messageService.getSentMessages(pageDTO);

        if (!data.isEmpty()) {
            count = data.get(0).getTotalCount();
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;
        }

        List<String> searchCategories = Arrays.asList("받은 사람", "내용");

        // 테이블
        model.addAttribute("tableHeader", header);
        model.addAttribute("tableData", data);

        // 페이지
        model.addAttribute("pages", pages);
        model.addAttribute("page", page);

        // 검색 결과
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
     * Block.
     *
     * @param c     the c
     * @param page  the page
     * @param model the model
     * @since 2024 -08-12
     */
    @GetMapping("/block")
    public void block(@AuthenticationPrincipal CustomUserDetails c,
                      @RequestParam(value = "page", defaultValue = "1") int page,
                      Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int count;
        int pages = 1;

        PageDTO pageDTO = new PageDTO();
        pageDTO.setMemberNo(memberNo);
        pageDTO.setPage(page);
        pageDTO.setStartEnd(page);

        List<String> header = Arrays.asList("차단된 닉네임", "차단 일자");
        List<MessageBlockDTO> data = messageService.getBlockList(pageDTO);

        if (!data.isEmpty()) {
            count = data.get(0).getTotalCount();
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;
        }

        // 테이블
        model.addAttribute("tableHeader", header);
        model.addAttribute("tableData", data);

        // 페이지
        model.addAttribute("pages", pages);
        model.addAttribute("page", page);
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


        if (messageService.sendMessage(messageDTO)) {
            return ResponseEntity.ok("쪽지를 보냈습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 회원입니다.");
        }
    }


    /**
     * Delete messages map.
     *
     * @param request the request
     * @return the map
     * @since 2024 -08-08
     */
    @PostMapping("/deleteMessages")
    @ResponseBody
    public Map<String, Boolean> deleteMessages(@RequestBody Map<String, List<Integer>> request) {

        List<Integer> messageNos = request.get("messageNos");

        Map<String, Boolean> result = new HashMap<>();
        result.put("success", messageService.deleteMessages(messageNos));
        return result;
    }

    /**
     * Block messages map.
     *
     * @param c       the c
     * @param request the request
     * @return the map
     * @since 2024 -08-10
     */
    @PostMapping("/blockMessages")
    @ResponseBody
    public Map<String, Object> blockMessages(@AuthenticationPrincipal CustomUserDetails c,
                                             @RequestBody Map<String, Object> request) {

        int blockerNo = c.getMemberDTO().getMemberNo();

        List<Integer> blockedNos = (List<Integer>) request.get("senderNos");
        boolean isBlocked = (boolean) request.get("isBlocked");

        Map<String, Object> result = new HashMap<>();

        for (int blockedNo : blockedNos) {

            if (blockedNo == blockerNo) {
                result.put("errorMessage", "자기 자신은 차단할 수 없습니다.");
                result.put("success", false);
                return result;
            }

            if (messageService.isAdmin(blockedNo).equals("ROLE_ADMIN")) {
                result.put("errorMessage", "관리자는 차단할 수 없습니다.");
                result.put("success", false);
                return result;
            }
        }

        boolean blockResult = false;
        if (isBlocked) {
            blockResult = messageService.unblockMessages(blockerNo, blockedNos);
        } else {
            try {
                blockResult = messageService.blockMessages(blockerNo, blockedNos);
            } catch (Exception e) {
                result.put("errorMessage", "이미 차단된 회원입니다.");
                result.put("success", false);
            }
        }

        if (blockResult) {
            result.put("message", isBlocked ? "차단이 해제되었습니다." : "회원이 차단되었습니다.");
            result.put("success", true);
        } else {
            result.put("errorMessage", isBlocked ? "차단 해제에 실패하였습니다." : "차단에 실패하였습니다.");
            result.put("success", false);
        }

        return result;
    }

    /**
     * Get message response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    // 웹소켓
    @GetMapping("/getMessage")
    public ResponseEntity<List<MessageDTO>> getMessage(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        return ResponseEntity.ok(messageService.getMessage(receiverNo));
    }

    /**
     * Get unread noti count response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @GetMapping("/getUnreadMessageCount")
    public ResponseEntity<Map<String, Integer>> getUnreadMessageCount(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        int count = messageService.getUnreadMessageCount(receiverNo);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Mark all as read message response entity.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-13
     */
    @PostMapping("/markAllAsReadMessage")
    public ResponseEntity<Map<String, Boolean>> markAllAsReadMessage(@AuthenticationPrincipal CustomUserDetails c) {
        int receiverNo = c.getMemberDTO().getMemberNo();
        boolean success = messageService.markAllAsReadMessage(receiverNo);
        return ResponseEntity.ok(Map.of("success", success));
    }
}
