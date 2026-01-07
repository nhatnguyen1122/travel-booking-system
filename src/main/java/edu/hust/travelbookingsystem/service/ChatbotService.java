package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.model.ChatbotResponse;
import edu.hust.travelbookingsystem.model.ChatbotResponse.ChatAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service layer for handling chatbot interactions.
 * Uses the TravelAssistant (LangChain4j AI Service) with RAG capabilities and tools.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final TravelAssistant travelAssistant;

    // Pattern to match action tags: [ACTION:type|label|url|icon]
    private static final Pattern ACTION_PATTERN = Pattern.compile(
            "\\[ACTION:([^|]+)\\|([^|]+)\\|([^|]+)\\|([^\\]]+)\\]"
    );

    /**
     * Process a user message and return the AI response with any suggested actions.
     *
     * @param userMessage The message from the user
     * @return ChatbotResponse with message and optional actions
     */
    public ChatbotResponse processMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new ChatbotResponse("Please enter a message so I can help you!", true);
        }

        try {
            log.debug("Processing user message: {}", userMessage);
            String response = travelAssistant.chat(userMessage);
            log.debug("AI response generated successfully");
            
            // Parse response to extract actions
            return parseResponseWithActions(response);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            return ChatbotResponse.builder()
                    .message("I apologize, but I'm experiencing technical difficulties. " +
                            "Please try again later or contact support at support@travelbooking.com")
                    .success(false)
                    .actions(List.of(ChatAction.builder()
                            .type("contact")
                            .label("Contact Support")
                            .url("/contact")
                            .icon("fa-envelope")
                            .build()))
                    .build();
        }
    }

    /**
     * Parse the AI response to extract action tags and create a structured response.
     *
     * @param response The raw AI response
     * @return ChatbotResponse with message and extracted actions
     */
    private ChatbotResponse parseResponseWithActions(String response) {
        List<ChatAction> actions = new ArrayList<>();
        Matcher matcher = ACTION_PATTERN.matcher(response);
        
        // Extract all actions from the response
        while (matcher.find()) {
            String type = matcher.group(1).trim();
            String label = matcher.group(2).trim();
            String url = matcher.group(3).trim();
            String icon = matcher.group(4).trim();
            
            actions.add(ChatAction.builder()
                    .type(type)
                    .label(label)
                    .url(url)
                    .icon(icon)
                    .build());
        }
        
        // Remove action tags from the message
        String cleanMessage = ACTION_PATTERN.matcher(response).replaceAll("").trim();
        
        return ChatbotResponse.builder()
                .message(cleanMessage)
                .success(true)
                .actions(actions.isEmpty() ? null : actions)
                .build();
    }

    /**
     * Get a welcome message for new chat sessions.
     *
     * @return ChatbotResponse with welcome message and quick actions
     */
    public ChatbotResponse getWelcomeMessage() {
        String welcomeMessage = "👋 Hello! Welcome to Travel Booking System!\n\n" +
                "I'm your AI travel assistant powered by advanced AI. I can help you with:\n" +
                "• 🏨 **Finding hotels** - I can search our database for available hotels\n" +
                "• ✈️ **Booking flights** - Find flights and check available seats\n" +
                "• 🌍 **Destination info** - Get details about Da Nang, Nha Trang, Phu Quoc, and more\n" +
                "• 💰 **Pricing & payments** - Understand costs and payment options\n" +
                "• 🎁 **Loyalty program** - Learn about rewards and promotions\n" +
                "• 🌐 **Travel tips** - I can search the web for travel information\n\n" +
                "Just ask me anything, and I'll help you plan your perfect trip!";

        List<ChatAction> quickActions = List.of(
                ChatAction.builder()
                        .type("booking")
                        .label("Start Booking")
                        .url("/booking")
                        .icon("fa-calendar-check")
                        .build(),
                ChatAction.builder()
                        .type("hotel")
                        .label("Browse Hotels")
                        .url("/hotel")
                        .icon("fa-hotel")
                        .build(),
                ChatAction.builder()
                        .type("flight")
                        .label("View Flights")
                        .url("/flight")
                        .icon("fa-plane")
                        .build()
        );

        return ChatbotResponse.builder()
                .message(welcomeMessage)
                .success(true)
                .actions(quickActions)
                .build();
    }
}
