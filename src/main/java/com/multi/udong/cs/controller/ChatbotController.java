package com.multi.udong.cs.controller;

import com.multi.udong.cs.model.dto.ChatbotRequest;
import com.multi.udong.cs.model.dto.ChatbotResponse;
import com.multi.udong.cs.service.ChatbotService;
import com.multi.udong.login.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * The type Chatbot controller.
 *
 * @author 김재식
 * @since 2024 -08-05
 */
@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * Get welcome message.
     *
     * @param c the c
     * @return the response entity
     * @since 2024 -08-05
     */
    @GetMapping("/welcome")
    public ResponseEntity<ChatbotResponse> getWelcomeMessage(@AuthenticationPrincipal CustomUserDetails c) {

        String userId;

        if (c != null && c.getMemberDTO().getMemberNo() != null) {
            userId = String.valueOf(c.getMemberDTO().getMemberNo());
        } else {
            userId = "0";
        }

        String welcomeMessage = chatbotService.getWelcomeMessage(userId);
        ChatbotResponse response = new ChatbotResponse();
        response.setMessage(welcomeMessage);

        return ResponseEntity.ok(response);
    }

    /**
     * Send message.
     *
     * @param c       the c
     * @param request the request
     * @return the response entity
     * @since 2024 -08-05
     */
    @PostMapping("/send")
    public ResponseEntity<ChatbotResponse> sendMessage(@AuthenticationPrincipal CustomUserDetails c,
                                                       @RequestBody ChatbotRequest request) {

        System.out.println("request : " + request);

        String userId;

        if (c != null && c.getMemberDTO().getMemberNo() != null) {
            userId = String.valueOf(c.getMemberDTO().getMemberNo());
        } else {
            userId = "0";
        }

        String chatbotResponse = chatbotService.sendMessage(userId, request.getMessage());

        System.out.println("chatbotResponse : " + chatbotResponse);

        ChatbotResponse response = new ChatbotResponse();
        response.setMessage(chatbotResponse);

        return ResponseEntity.ok(response);
    }
}
