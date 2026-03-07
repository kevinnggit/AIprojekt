package com.nspace.dto;

/**
 * Datenobjekt (DTO) für eine Registrierungsanfrage.
 *
 * <p>Überträgt die nötigen Daten zur Anlage eines neuen Benutzerkontos.
 * Das Feld {@code role} ermöglicht es dem Admin-Endpunkt, gezielt Benutzer
 * mit unterschiedlichen Rollen ({@code ROLE_USER}, {@code ROLE_ADMIN}) zu erstellen.</p>
 *
 * @param username der gewünschte Benutzername (muss eindeutig sein)
 * @param password das Passwort im Klartext (wird vor der Speicherung mit BCrypt gehasht)
 * @param role     die zuzuweisende Rolle (z. B. {@code ROLE_USER} oder {@code ROLE_ADMIN})
 */
public record RegisterRequest(String username, String password, String role) {
}
