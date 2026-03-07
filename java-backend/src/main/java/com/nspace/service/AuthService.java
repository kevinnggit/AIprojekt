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

/**
 * Service für Authentifizierung und Benutzerregistrierung.
 *
 * <p>Kapselt die Geschäftslogik für Login und Registrierung. Beim Login wird
 * der {@link AuthenticationManager} verwendet, der intern die Credentials gegen
 * die Datenbank prüft. Bei Erfolg wird ein JWT generiert und zurückgegeben.
 * Passwörter werden niemals im Klartext gespeichert, sondern stets mit BCrypt gehasht.</p>
 */
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

    /**
     * Authentifiziert einen Benutzer und gibt bei Erfolg einen JWT zurück.
     *
     * <p>Der {@link AuthenticationManager} wirft eine Ausnahme wenn die Credentials
     * ungültig sind, sodass keine manuelle Fehlerbehandlung nötig ist.</p>
     *
     * @param request die Anmeldedaten (Benutzername und Passwort)
     * @return eine {@link LoginResponse} mit dem generierten JWT
     */
    public LoginResponse login(LoginRequest request) {
        // Wirft BadCredentialsException wenn Benutzername/Passwort falsch
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtUtil.generateToken(userDetails);
        return new LoginResponse(token);
    }

    /**
     * Registriert einen neuen Standardbenutzer mit der Rolle {@code ROLE_USER}.
     *
     * @param request die Registrierungsdaten
     * @return eine {@link LoginResponse} mit einem direkt generierten JWT (Auto-Login)
     */
    public LoginResponse register(RegisterRequest request) {
        return registerUser(request.username(), request.password(), "ROLE_USER");
    }

    /**
     * Registriert einen neuen Administrator-Benutzer mit der Rolle {@code ROLE_ADMIN}.
     * Dieser Endpunkt ist ausschließlich über den geschützten Admin-Controller erreichbar.
     *
     * @param request die Registrierungsdaten
     * @return eine {@link LoginResponse} (Token kann vom Aufrufer ignoriert werden)
     */
    public LoginResponse registerAdmin(RegisterRequest request) {
        return registerUser(request.username(), request.password(), "ROLE_ADMIN");
    }

    /**
     * Interne Hilfsmethode zur Benutzeranlage mit beliebiger Rolle.
     *
     * <p>Prüft zunächst auf Namenskonflikte. Das Passwort wird mit BCrypt gehasht
     * bevor es in der Datenbank gespeichert wird. Nach dem Speichern wird
     * automatisch ein JWT generiert (konsistentes Verhalten mit dem öffentlichen
     * Registrierungsendpunkt). Beim Admin-seitigen Anlegen kann das Token ignoriert werden.</p>
     *
     * @param username der gewünschte Benutzername
     * @param password das Klartext-Passwort
     * @param role     die zuzuweisende Rolle (z. B. {@code ROLE_USER})
     * @return eine {@link LoginResponse} mit JWT
     * @throws IllegalArgumentException wenn der Benutzername bereits vergeben ist
     */
    private LoginResponse registerUser(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // BCrypt-Hash, niemals Klartext
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
