package edu.hust.travelbookingsystem.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for chatbot messages.
 * Includes the message content, success status, and optional navigation actions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatbotResponse {

    /**
     * The AI-generated message content
     */
    private String message;

    /**
     * Whether the request was processed successfully
     */
    private boolean success;

    /**
     * Optional list of suggested actions/navigation links
     */
    private List<ChatAction> actions;

    /**
     * Simple constructor for basic responses
     */
    public ChatbotResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * Represents a clickable action/link in the chatbot response
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatAction {
        /**
         * Display label for the action button
         */
        private String label;

        /**
         * URL to navigate to when clicked
         */
        private String url;

        /**
         * Icon class (FontAwesome) for the button
         */
        private String icon;

        /**
         * Action type: 'navigate', 'booking', 'info'
         */
        private String type;

        /**
         * Optional context data to pass (e.g., hotelId, flightId)
         */
        private String context;
    }
}
