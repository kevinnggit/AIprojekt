package com.nspace.controller;

import com.nspace.model.GlobalConfig;
import com.nspace.model.User;
import com.nspace.repository.ConfigRepository;
import com.nspace.repository.UserRepository;

import com.nspace.service.AuthService;
import com.nspace.service.ConfigService;
import com.nspace.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000") // Allow Vue Frontend
@PreAuthorize("hasRole('ADMIN')") // Additional layer of security
public class AdminController {

    private final ConfigService configService;
    private final ConfigRepository configRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    public AdminController(ConfigService configService, ConfigRepository configRepository, AuthService authService,
            UserRepository userRepository) {
        this.configService = configService;
        this.configRepository = configRepository;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // --- CONFIGURATION ---

    @GetMapping("/config")
    public List<GlobalConfig> getAllConfig() {
        return configRepository.findAll();
    }

    @PostMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, String> payload) {
        String key = payload.get("key");
        String value = payload.get("value");
        String description = payload.get("description");

        if (key == null || value == null) {
            return ResponseEntity.badRequest().body("Key and Value are required");
        }

        configService.setValue(key, value, description);
        return ResponseEntity.ok().build();
    }

    // --- USER MANAGEMENT ---

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String password = payload.get("password");
            String role = payload.get("role"); // "ADMIN" or "USER"

            if (username == null || password == null || role == null) {
                return ResponseEntity.badRequest().body("Username, Password and Role are required.");
            }

            RegisterRequest req = new RegisterRequest(username, password, role);

            if ("ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)) {
                authService.registerAdmin(req);
            } else {
                authService.register(req);
            }

            return ResponseEntity.ok().body(Map.of("message", "User created successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
