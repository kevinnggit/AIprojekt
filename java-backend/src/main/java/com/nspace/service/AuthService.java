package com.nspace.service;

import com.nspace.dto.LoginRequest;
import com.nspace.dto.LoginResponse;
import com.nspace.dto.RegisterRequest;
import com.nspace.model.User;
import com.nspace.repository.UserRepository;
import com.nspace.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        // If authentication successful, generate token
        // Use user details or optional logic
        var user = userRepository.findByUsername(request.username()).orElseThrow();
        // Create UserDetails object or just pass custom one if implemented
        // For simplicity, we can load UserDetails via the service or just construct
        // basic one here for token gen if needed,
        // but easier to rely on what JwtUtil expects (UserDetails).
        // Let's create a UserDetails implementation on the fly or load it.
        // Actually, CustomUserDetailsService does the loading.
        // Let's manually construct it for now to match JwtUtil signature:
        org.springframework.security.core.userdetails.UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new java.util.ArrayList<>());

        String token = jwtUtil.generateToken(userDetails);
        return new LoginResponse(token);
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        var user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role() == null ? "ROLE_USER" : request.role());
        userRepository.save(user);
    }
}
