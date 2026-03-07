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

/**
 * Administrativer REST-Controller für Konfiguration und Benutzerverwaltung.
 *
 * <p>Alle Endpunkte dieses Controllers sind doppelt abgesichert: Zum einen durch
 * die URL-Regel {@code /api/admin/**} in der {@code SecurityConfig}, zum anderen
 * durch die Annotation {@code @PreAuthorize("hasRole('ADMIN')")}, die sicherstellt,
 * dass nur Benutzer mit der Rolle {@code ROLE_ADMIN} Zugriff erhalten.</p>
 */
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

    /**
     * Gibt alle globalen Konfigurationseinträge zurück.
     *
     * @return Liste aller {@link GlobalConfig}-Einträge aus der Datenbank
     */
    @GetMapping("/config")
    public List<GlobalConfig> getAllConfig() {
        return configRepository.findAll();
    }

    /**
     * Erstellt oder aktualisiert einen Konfigurationswert anhand eines Schlüssels.
     *
     * @param payload JSON-Objekt mit den Feldern {@code key}, {@code value} und optional {@code description}
     * @return HTTP 200 bei Erfolg oder HTTP 400 wenn Pflichtfelder fehlen
     */
    @PostMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, String> payload) {
        String key = payload.get("key");
        String value = payload.get("value");
        String description = payload.get("description");

        // key und value sind Pflichtfelder – ohne sie ist die Anfrage ungültig
        if (key == null || value == null) {
            return ResponseEntity.badRequest().body("Key and Value are required");
        }

        configService.setValue(key, value, description);
        return ResponseEntity.ok().build();
    }

    // --- USER MANAGEMENT ---

    /**
     * Gibt eine Liste aller registrierten Benutzer zurück.
     *
     * @return Liste aller {@link User}-Entitäten
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Legt einen neuen Benutzer mit einer explizit angegebenen Rolle an.
     *
     * <p>Abhängig vom Wert des Felds {@code role} wird entweder {@code registerAdmin}
     * oder das reguläre {@code register} aufgerufen. So kann ein Admin gezielt
     * weitere Admins anlegen, ohne den öffentlichen Registrierungsweg zu nutzen.</p>
     *
     * @param payload JSON-Objekt mit {@code username}, {@code password} und {@code role}
     * @return HTTP 200 mit Bestätigungsnachricht oder HTTP 400 mit Fehlerbeschreibung
     */
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

            // Je nach Rolle wird der passende Registrierungsweg aufgerufen
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
