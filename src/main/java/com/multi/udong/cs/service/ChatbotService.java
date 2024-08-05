package com.multi.udong.cs.service;

/**
 * The interface Chatbot service.
 *
 * @author 김재식
 * @since 2024 -08-05
 */
public interface ChatbotService {

    String getWelcomeMessage(String userId);

    String sendMessage(String userId, String message);
}
