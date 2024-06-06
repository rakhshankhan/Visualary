package com.rakhshankhan.visualary.controller;

import com.rakhshankhan.visualary.model.entity.User;
import com.rakhshankhan.visualary.model.request.UserLoginRequest;
import com.rakhshankhan.visualary.model.request.UserRegistrationRequest;
import com.rakhshankhan.visualary.service.UserService;
import com.rakhshankhan.visualary.service.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        try {
            User user = new User();
            user.setFirstName(userRegistrationRequest.getFirstName());
            user.setLastName(userRegistrationRequest.getLastName());
            user.setEmail(userRegistrationRequest.getEmail());
            user.setPassword(userRegistrationRequest.getPassword());

            return userService.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginRequest loginRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            String jwt = jwtUtil.generateToken(loginRequest.getEmail());

            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return "Login Successful";
        } catch (AuthenticationException e) {
            return "Login failed due to an invalid email or password";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("jwt", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return "Logout Successful";
        } catch (Exception e) {
            return "Logout failed";
        }
    }

}
