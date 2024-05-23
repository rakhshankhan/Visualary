package com.rakhshankhan.visualary.controller;

import com.rakhshankhan.visualary.model.dto.ChatDTO;
import com.rakhshankhan.visualary.model.request.CohereRequest;
import com.rakhshankhan.visualary.service.CohereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class CohereController {

    private final CohereService cohereService;

    @Autowired
    private CohereController(CohereService cohereService) {
        this.cohereService = cohereService;
    }

    @PostMapping
    public ResponseEntity<ChatDTO> generateText(@RequestBody CohereRequest request) {
        try {
            ChatDTO response = cohereService.generateText(request.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearChatHistory() {
        try {
            cohereService.clearChatHistory();
            return ResponseEntity.ok("Chat history cleared successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to clear chat history.");
        }
    }
}
