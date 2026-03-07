package com.nspace.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Hilfsklasse für alle JWT-bezogenen Operationen (Generierung, Validierung, Parsing).
 *
 * <p>Verwendet HMAC-SHA256 als Signaturalgorithmus. Der geheime Schlüssel wird
 * aus der Anwendungskonfiguration ({@code jwt.secret}) gelesen und darf niemals
 * im Quellcode festkodiert werden. Generierte Token sind 10 Stunden gültig.</p>
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Erzeugt den kryptografischen Signierschlüssel aus dem konfigurierten Geheimnis.
     * Wird intern bei jeder Token-Operation verwendet.
     *
     * @return der HMAC-SHA-Schlüssel
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extrahiert den Benutzernamen (Subject) aus einem JWT.
     *
     * @param token der JWT als kompakter String
     * @return der im Token enthaltene Benutzername
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrahiert das Ablaufdatum aus einem JWT.
     *
     * @param token der JWT als kompakter String
     * @return das Ablaufdatum des Tokens
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generischer Extraktor für beliebige Claims aus einem JWT.
     *
     * <p>Ermöglicht typsicheres Lesen einzelner Claims via Funktionsreferenz,
     * z. B. {@code extractClaim(token, Claims::getSubject)}.</p>
     *
     * @param <T>            Rückgabetyp des Claims
     * @param token          der JWT als kompakter String
     * @param claimsResolver Funktion zur Extraktion des gewünschten Claims
     * @return der extrahierte Wert
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parst den JWT und gibt alle enthaltenen Claims zurück.
     * Wirft eine Ausnahme wenn das Token ungültig oder abgelaufen ist.
     *
     * @param token der JWT als kompakter String
     * @return die geparsten {@link Claims}
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Prüft, ob ein Token bereits abgelaufen ist.
     *
     * @param token der JWT als kompakter String
     * @return {@code true} wenn das Token abgelaufen ist
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generiert einen neuen JWT für den angegebenen Benutzer.
     *
     * <p>Die Rolle wird als zusätzlicher Claim im Token gespeichert,
     * damit das Frontend Rolleninformationen ohne weiteren API-Aufruf erhält.</p>
     *
     * @param userDetails die Benutzerdaten aus Spring Security
     * @return der unterzeichnete JWT als kompakter String
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Rolle als eigener Claim hinterlegen, damit das Frontend sie auslesen kann
        claims.put("role", userDetails.getAuthorities().toString());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Erstellt und signiert den eigentlichen JWT mit allen Claims und einer Gültigkeitsdauer.
     *
     * @param claims  die in den Token einzubettenden Zusatzinformationen
     * @param subject der Benutzername (Subject des Tokens)
     * @return der fertige JWT als kompakter String
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validiert ein Token gegen die Benutzerdaten aus der Datenbank.
     *
     * <p>Ein Token ist gültig wenn der Benutzername im Token mit dem aus der DB übereinstimmt
     * und das Token noch nicht abgelaufen ist.</p>
     *
     * @param token       der zu prüfende JWT
     * @param userDetails die aktuellen Benutzerdaten aus der Datenbank
     * @return {@code true} wenn das Token gültig ist
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
