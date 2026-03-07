package com.nspace.dto;

/**
 * Datenobjekt (DTO) für die Anmeldeantwort.
 *
 * <p>Wird nach erfolgreicher Authentifizierung an den Client zurückgegeben.
 * Der enthaltene JWT wird vom Client bei nachfolgenden Anfragen
 * im {@code Authorization: Bearer <token>}-Header mitgesendet.</p>
 *
 * @param token der generierte JSON Web Token (JWT)
 */
public record LoginResponse(String token) {
}
