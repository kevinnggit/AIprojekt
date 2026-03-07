package com.nspace.security;

import com.nspace.model.User;
import com.nspace.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementierung von {@link UserDetailsService} zum Laden von Benutzerdaten aus der Datenbank.
 *
 * <p>Spring Security ruft diese Klasse auf, um bei der Authentifizierung die
 * Benutzerdaten (inkl. Passwort-Hash und Rollen) aus der Datenbank zu laden.
 * Die Datenbankrolle (z. B. {@code ROLE_ADMIN}) wird dabei in eine Spring-Security-Authority
 * umgewandelt, die dann in Zugriffsprüfungen wie {@code hasRole("ADMIN")} ausgewertet wird.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Lädt einen Benutzer anhand seines Benutzernamens aus der Datenbank.
     *
     * <p>Die in der DB gespeicherte Rolle (z. B. {@code ROLE_ADMIN}) wird als
     * {@link org.springframework.security.core.authority.SimpleGrantedAuthority} übergeben.
     * Das Präfix {@code ROLE_} ist dabei von Spring Security vorgeschrieben.</p>
     *
     * @param username der zu ladende Benutzername
     * @return ein {@link UserDetails}-Objekt mit Benutzername, Passwort-Hash und Rollen
     * @throws UsernameNotFoundException wenn kein Benutzer mit diesem Namen existiert
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                // SECURITY CRITICAL: Mapping der DB-Rolle (z.B. "ROLE_ADMIN") zur Spring
                // Authority.
                // Ohne das weiß Spring Security nicht, dass dieser User Admin-Rechte hat!
                java.util.List
                        .of(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole())));
    }
}
