package edu.hust.travelbookingsystem.controller;

import edu.hust.travelbookingsystem.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling chatbot requests.
 * Provides both REST API and page rendering endpoints.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * Render the chatbot page.
     *
     * @return The chatbot HTML template
     */
    @GetMapping("/chatbot")
    public String chatbotPage() {
        return "chatbot";
    }

    /**
     * REST endpoint to process chat messages.
     *
     * @param request Map containing the user's message
     * @return AI-generated response
     */
    @PostMapping("/api/chatbot/message")
    @ResponseBody
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        log.info("Received chat message: {}", userMessage);

        String response = chatbotService.processMessage(userMessage);
        return ResponseEntity.ok(new ChatResponse(response, true));
    }

    /**
     * REST endpoint to get welcome message.
     *
     * @return Welcome message
     */
    @GetMapping("/api/chatbot/welcome")
    @ResponseBody
    public ResponseEntity<ChatResponse> getWelcomeMessage() {
        String welcomeMessage = chatbotService.getWelcomeMessage();
        return ResponseEntity.ok(new ChatResponse(welcomeMessage, true));
    }

    /**
     * Response DTO for chat messages.
     */
    public record ChatResponse(String message, boolean success) {}
}
