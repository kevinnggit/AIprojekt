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
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtUtil.generateToken(userDetails);
        return new LoginResponse(token);
    }

    public LoginResponse register(RegisterRequest request) {
        return registerUser(request.username(), request.password(), "ROLE_USER");
    }

    public LoginResponse registerAdmin(RegisterRequest request) {
        return registerUser(request.username(), request.password(), "ROLE_ADMIN");
    }

    private LoginResponse registerUser(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);

        // Auto-login after register? Or just return token.
        // For admin creating other admins, we don't want to login as them. We just want
        // to return "Success".
        // But for consistency with existing public register, we generate token.
        // We can ignore the token in AdminController.

        var userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails);
        return new LoginResponse(token);
    }
}
