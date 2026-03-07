package com.nspace.dto;

/**
 * Datenobjekt (DTO) für eine Anmeldeanfrage.
 *
 * <p>Enthält die vom Client übermittelten Anmeldeinformationen.
 * Als Java Record ist dieses Objekt unveränderlich (immutable).</p>
 *
 * @param username der Benutzername
 * @param password das Klartext-Passwort (wird intern gegen den BCrypt-Hash geprüft)
 */
public record LoginRequest(String username, String password) {
}
