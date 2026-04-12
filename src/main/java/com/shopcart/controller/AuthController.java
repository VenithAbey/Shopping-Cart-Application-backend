package com.shopcart.controller;

import com.shopcart.dto.AuthResponse;
import com.shopcart.dto.LoginRequest;
import com.shopcart.dto.SignupRequest;
import com.shopcart.entity.User;
import com.shopcart.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/admin-signup")
    public ResponseEntity<AuthResponse> adminSignup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signupAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse.UserDto> profile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getProfile(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // JWT is stateless — client removes token
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
