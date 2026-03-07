package com.nspace.security;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP-Filter zur JWT-basierten Authentifizierung jeder eingehenden Anfrage.
 *
 * <p>Dieser Filter wird einmalig pro Request ausgeführt ({@link OncePerRequestFilter})
 * und ist in der Spring Security-Filterkette vor dem Standard-Login-Filter platziert.
 * Er extrahiert den JWT aus dem {@code Authorization}-Header, validiert ihn und
 * setzt die Authentifizierung im {@link SecurityContextHolder}, sodass nachfolgende
 * Sicherheitsprüfungen den Benutzer korrekt erkennen.</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Kernlogik des Filters: Token extrahieren, validieren und Authentifizierung setzen.
     *
     * <p>Wenn kein gültiger Token vorhanden ist, wird die Anfrage ohne Authentifizierung
     * weitergeLeitet – Spring Security entscheidet dann anhand der Sicherheitsregeln,
     * ob der Zugriff erlaubt ist.</p>
     *
     * @param request     die eingehende HTTP-Anfrage
     * @param response    die ausgehende HTTP-Antwort
     * @param filterChain die Filterkette zur Weiterleitung der Anfrage
     * @throws ServletException bei Servlet-Fehlern
     * @throws IOException      bei Ein-/Ausgabefehlern
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization"); // Holt "Bearer eyJhbGci..."
        System.out.println("JwtAuthFilter processing: " + request.getRequestURI());

        String username = null;
        String jwt = null;

        // 1. Token aus Header extrahieren
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // "Bearer " abschneiden
            try {
                username = jwtUtil.extractUsername(jwt); // Wer ist das?
            } catch (Exception e) {
                // Ungültiges oder abgelaufenes Token – Authentifizierung wird nicht gesetzt,
                // die Anfrage läuft ohne Benutzerkontext weiter
            }
        }

        // 2. Nur setzen wenn Benutzer identifiziert wurde und noch keine Authentifizierung vorliegt
        // Die Prüfung auf null verhindert eine doppelte Authentifizierung pro Request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Token ist gültig – Authentifizierungsobjekt erstellen und im Kontext speichern
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
