package com.multi.udong.cs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.udong.cs.model.dto.Bubble;
import com.multi.udong.cs.model.dto.ChatbotRequest;
import com.multi.udong.cs.model.dto.TextData;
import com.multi.udong.cs.openFeign.ChatbotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

/**
 * The type Chatbot service.
 *
 * @author 김재식
 * @since 2024 -08-05
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService{

    private final ChatbotClient chatbotClient;
    private final ObjectMapper objectMapper;

    @Value("${naver.clova.chatbot.secret}")
    private String secretKey;

    /**
     * Get welcome message.
     *
     * @param userId the member id
     * @return string string
     * @since 2024 -08-05
     */
    @Override
    public String getWelcomeMessage(String userId) {

        ChatbotRequest request = new ChatbotRequest();
        request.setVersion("v2");
        request.setUserId(userId);
        request.setTimestamp(System.currentTimeMillis());
        request.setBubbles(Collections.singletonList(new Bubble()));
        request.setEvent("open");

        String requestBody = convertToJsonString(request);
        String signature = generateSignature(requestBody);

        return chatbotClient.chatbotRequest(signature, request);
    }

    /**
     * Send message.
     *
     * @param userId the user id
     * @since 2024 -08-05
     */
    @Override
    public String sendMessage(String userId, String message) {

        ChatbotRequest request = new ChatbotRequest();
        request.setVersion("v2");
        request.setUserId(userId);
        request.setTimestamp(System.currentTimeMillis());
        request.setEvent("send");

        Bubble bubble = new Bubble();
        bubble.setType("text");

        TextData textData = new TextData();
        textData.setDescription(message);
        bubble.setData(textData);

        request.setBubbles(Collections.singletonList(bubble));

        System.out.println("request : " + request);

        String requestBody = convertToJsonString(request);
        String signature = generateSignature(requestBody);

        return chatbotClient.chatbotRequest(signature, request);
    }

    /**
     * Convert to json string string.
     *
     * @param request the request
     * @return the string
     * @since 2024 -08-05
     */
    public String convertToJsonString(ChatbotRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환에 실패하였습니다.", e);
        }
    }

    /**
     * Generate signature string.
     *
     * @param requestBody the request body
     * @return the string
     * @since 2024 -08-05
     */
    public String generateSignature(String requestBody) {

        try {

            byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signature = mac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}
