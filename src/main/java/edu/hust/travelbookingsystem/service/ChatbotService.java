package edu.hust.travelbookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for handling chatbot interactions.
 * Uses the TravelAssistant (LangChain4j AI Service) with RAG capabilities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final TravelAssistant travelAssistant;

    /**
     * Process a user message and return the AI response.
     *
     * @param userMessage The message from the user
     * @return AI-generated response based on the knowledge base
     */
    public String processMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please enter a message so I can help you!";
        }

        try {
            log.debug("Processing user message: {}", userMessage);
            String response = travelAssistant.chat(userMessage);
            log.debug("AI response generated successfully");
            return response;
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            return "I apologize, but I'm experiencing technical difficulties. " +
                    "Please try again later or contact support at support@travelbooking.com";
        }
    }

    /**
     * Get a welcome message for new chat sessions.
     *
     * @return Welcome message string
     */
    public String getWelcomeMessage() {
        return "👋 Hello! Welcome to Travel Booking System!\n\n" +
                "I'm your AI travel assistant. I can help you with:\n" +
                "• Finding destinations (Da Nang, Nha Trang, Phu Quoc, Ha Long, Hoi An, Sapa)\n" +
                "• Booking flights and hotels\n" +
                "• Understanding our pricing and payment options\n" +
                "• Loyalty program and promotions\n" +
                "• General travel questions\n\n" +
                "How can I assist you today?";
    }
}
