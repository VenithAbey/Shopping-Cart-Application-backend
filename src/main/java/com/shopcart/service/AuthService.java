package com.shopcart.service;

import com.shopcart.dto.AuthResponse;
import com.shopcart.dto.LoginRequest;
import com.shopcart.dto.SignupRequest;
import com.shopcart.entity.User;
import com.shopcart.repository.UserRepository;
import com.shopcart.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.user)
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDto.from(user))
                .message("User registered successfully")
                .build();
    }

    public AuthResponse signupAdmin(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.admin)
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDto.from(user))
                .message("Admin registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDto.from(user))
                .message("Login successful")
                .build();
    }

    public AuthResponse.UserDto getProfile(User user) {
        return AuthResponse.UserDto.from(user);
    }

    public AuthResponse firebaseLogin(String firebaseHashToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=AIzaSyB85Lk_p5D-MsKFJ9_p0JHrMGRBwe_-OZE";
            Map<String, String> requestBody = Map.of("idToken", firebaseHashToken);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> users = (List<Map<String, Object>>) response.getBody().get("users");
                if (users != null && !users.isEmpty()) {
                    Map<String, Object> firebaseUser = users.get(0);
                    String email = (String) firebaseUser.get("email");
                    String displayName = (String) firebaseUser.get("displayName");
                    
                    if (email == null) {
                        throw new RuntimeException("Email not found in Firebase token");
                    }
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = email.split("@")[0];
                    }

                    // Auto-register user if they do not exist
                    User localUser = userRepository.findByEmail(email).orElse(null);
                    if (localUser == null) {
                        localUser = User.builder()
                                .name(displayName)
                                .email(email)
                                // Generate a completely random un-guessable password since they use OAuth
                                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                .role(User.Role.user)
                                .build();
                        localUser = userRepository.save(localUser);
                    }

                    // Generate local JWT mapping to their MySQL account
                    String token = jwtUtil.generateToken(localUser.getEmail());

                    return AuthResponse.builder()
                            .token(token)
                            .user(AuthResponse.UserDto.from(localUser))
                            .message("Firebase Login Successful")
                            .build();
                }
            }
            throw new RuntimeException("Invalid Firebase token response");
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate Firebase Token: " + e.getMessage());
        }
    }
}
