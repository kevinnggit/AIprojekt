package com.nspace.config;

import com.nspace.security.CustomUserDetailsService;
import com.nspace.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Zentrale Sicherheitskonfiguration der Anwendung.
 *
 * <p>Diese Klasse definiert die gesamte HTTP-Sicherheitsstrategie: Welche Endpunkte
 * öffentlich zugänglich sind, welche eine Admin-Rolle erfordern, wie CORS konfiguriert
 * wird und dass die Anwendung vollständig zustandslos (stateless) operiert.
 * Der JWT-Filter wird hier in die Spring Security-Filterkette eingehängt.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Konfiguriert die HTTP-Sicherheitskette mit Zugriffs- und Sessionregeln.
     *
     * <p>CSRF ist deaktiviert, da die API zustandslos (stateless) ist und JWT
     * als Schutzmechanismus dient. Der JWT-Filter wird vor dem Standard-Login-Filter
     * platziert, damit Token-basierte Anfragen korrekt verarbeitet werden.</p>
     *
     * @param http das zu konfigurierende {@link HttpSecurity}-Objekt
     * @return die fertig konfigurierte {@link SecurityFilterChain}
     * @throws Exception bei Konfigurationsfehlern
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()

                        // Appointment Security Rules
                        // Admin actions (Delete & Confirm)
                        .requestMatchers(new AntPathRequestMatcher("/api/termine/*/confirm", "PUT")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/termine/*", "DELETE")).hasRole("ADMIN")

                        // Public actions (Create & List) - Allow everything else under /api/termine
                        .requestMatchers(new AntPathRequestMatcher("/api/termine/**")).permitAll()

                        // Portfolio Public
                        .requestMatchers(new AntPathRequestMatcher("/api/portfolio/**")).permitAll()

                        // Admin Area (Global)
                        .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")

                        // Error handling
                        .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()

                        // Default: Authenticated
                        .anyRequest().authenticated())
                // Keine HTTP-Session – jede Anfrage muss sich selbst über JWT authentifizieren
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                // JWT-Filter läuft VOR dem Standard-UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Erstellt einen {@link AuthenticationProvider}, der Benutzer anhand der Datenbank
     * und BCrypt-verschlüsselter Passwörter verifiziert.
     *
     * @return konfigurierter {@link DaoAuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Stellt den {@link AuthenticationManager} als Spring-Bean bereit,
     * damit er in Services per Dependency Injection genutzt werden kann.
     *
     * @param config die von Spring verwaltete Authentifizierungskonfiguration
     * @return der konfigurierte {@link AuthenticationManager}
     * @throws Exception bei Initialisierungsfehlern
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Definiert BCrypt als Passwort-Hashing-Algorithmus.
     * BCrypt ist bewusst rechenintensiv, um Brute-Force-Angriffe zu erschweren.
     *
     * @return eine {@link BCryptPasswordEncoder}-Instanz
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfiguriert die CORS-Richtlinien für die gesamte API.
     *
     * <p>Alle Ursprünge sind erlaubt, um die Entwicklung mit dem Vue-Frontend
     * zu vereinfachen. In einer Produktionsumgebung sollte dies auf bekannte
     * Origins eingeschränkt werden.</p>
     *
     * @return die CORS-Konfigurationsquelle
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
