package com.nspace.controller;

import com.nspace.dto.LoginRequest;
import com.nspace.dto.LoginResponse;
import com.nspace.dto.RegisterRequest;
import com.nspace.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-Controller für Authentifizierungsoperationen.
 *
 * <p>Stellt öffentliche Endpunkte für die Benutzeranmeldung und -registrierung bereit.
 * Diese Endpunkte sind in der {@code SecurityConfig} ausdrücklich ohne Authentifizierung
 * zugänglich gemacht ({@code permitAll()}).</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Meldet einen Benutzer an und liefert bei Erfolg ein JWT zurück.
     *
     * @param request Anmeldedaten (Benutzername und Passwort)
     * @return HTTP 200 mit dem generierten JWT-Token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registriert einen neuen Benutzer mit der Standardrolle {@code ROLE_USER}.
     *
     * @param request Registrierungsdaten (Benutzername, Passwort, Rolle)
     * @return HTTP 200 mit einer Bestätigungsnachricht
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }
}
