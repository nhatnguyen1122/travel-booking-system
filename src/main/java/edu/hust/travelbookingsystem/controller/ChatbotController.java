package edu.hust.travelbookingsystem.controller;

import edu.hust.travelbookingsystem.model.ChatbotResponse;
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
     * Returns AI response with optional navigation actions.
     *
     * @param request Map containing the user's message
     * @return ChatbotResponse with message and actions
     */
    @PostMapping("/api/chatbot/message")
    @ResponseBody
    public ResponseEntity<ChatbotResponse> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        log.info("Received chat message: {}", userMessage);

        ChatbotResponse response = chatbotService.processMessage(userMessage);
        return ResponseEntity.ok(response);
    }

    /**
     * REST endpoint to get welcome message with quick actions.
     *
     * @return ChatbotResponse with welcome message and actions
     */
    @GetMapping("/api/chatbot/welcome")
    @ResponseBody
    public ResponseEntity<ChatbotResponse> getWelcomeMessage() {
        ChatbotResponse response = chatbotService.getWelcomeMessage();
        return ResponseEntity.ok(response);
    }
}
