package com.rakhshankhan.visualary.controller;

import com.rakhshankhan.visualary.model.dto.ChatDTO;
import com.rakhshankhan.visualary.model.dto.ChatInfoDTO;
import com.rakhshankhan.visualary.model.entity.User;
import com.rakhshankhan.visualary.model.request.CohereRequest;
import com.rakhshankhan.visualary.service.CohereService;
import com.rakhshankhan.visualary.service.UserService;
import com.rakhshankhan.visualary.service.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class CohereController {

    private final CohereService cohereService;

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @Autowired
    private CohereController(CohereService cohereService, UserService userService, JwtUtil jwtUtil) {
        this.cohereService = cohereService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<ChatDTO> generateText(
            @PathVariable Integer chatId,
            @RequestBody CohereRequest cohereRequest,
            HttpServletRequest httpRequest) {
        try {
            String jwt;
            String userEmail = null;

            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if (cookie.getName().equals("jwt")) {
                        jwt = cookie.getValue();

                        try {
                            userEmail = jwtUtil.validateTokenAndGetSubject(jwt);
                        } catch (Exception e) {
                            throw new RuntimeException("JWT token was not found.");
                        }

                        break;
                    }
                }

            }

            User user = userService.findUserByEmail(userEmail);

            ChatDTO response = cohereService.generateText(cohereRequest.getMessage(), user.getId(), chatId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChatInfoDTO>> getUserChats(HttpServletRequest httpRequest) {
        try {
            String jwt;
            String userEmail = null;

            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if (cookie.getName().equals("jwt")) {
                        jwt = cookie.getValue();

                        try {
                            userEmail = jwtUtil.validateTokenAndGetSubject(jwt);
                        } catch (Exception e) {
                            throw new RuntimeException("JWT token was not found.");
                        }

                        break;
                    }
                }

            }

            User user = userService.findUserByEmail(userEmail);

            List<ChatInfoDTO> response = cohereService.getAllUserChats(user.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{chatId}/clear")
    public ResponseEntity<String> deleteChat(@PathVariable Integer chatId, HttpServletRequest httpRequest) {
        try {
            String jwt;
            String userEmail = null;

            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if (cookie.getName().equals("jwt")) {
                        jwt = cookie.getValue();

                        try {
                            userEmail = jwtUtil.validateTokenAndGetSubject(jwt);
                        } catch (Exception e) {
                            throw new RuntimeException("JWT token was not found.");
                        }

                        break;
                    }
                }

            }

            User user = userService.findUserByEmail(userEmail);

            cohereService.deleteUserChatById(user.getId(), chatId);
            return ResponseEntity.ok("Chat history cleared successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body("Failed to clear chat history.");
        }
    }

    @DeleteMapping("/all/clear")
    public ResponseEntity<String> deleteAllChatsForUser(HttpServletRequest request) {
        try {
            String jwt;
            String userEmail = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("jwt")) {
                        jwt = cookie.getValue();

                        try {
                            userEmail = jwtUtil.validateTokenAndGetSubject(jwt);
                        } catch (Exception e) {
                            throw new RuntimeException("JWT token was not found.");
                        }

                        break;
                    }
                }

            }

            User user = userService.findUserByEmail(userEmail);

            cohereService.deleteAllUserChats(user.getId());
            return ResponseEntity.ok("User's chats cleared successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body("Failed to clear user's chats");
        }
    }
}
