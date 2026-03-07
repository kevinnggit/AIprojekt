package com.nspace.dto;

import java.time.LocalDateTime;

/**
 * Datenobjekt (DTO) für eine eingehende Terminbuchungsanfrage.
 *
 * <p>Enthält alle vom Benutzer eingegebenen Daten, die zur Erstellung eines neuen
 * Termins erforderlich sind. Die Endzeit wird serverseitig berechnet (Startzeit + 1 Stunde),
 * der Status wird automatisch auf {@code PENDING} gesetzt.</p>
 *
 * @param name      vollständiger Name des Anfragenden
 * @param email     E-Mail-Adresse für eventuelle Benachrichtigungen
 * @param topic     Thema oder Gesprächsgegenstand des Termins
 * @param startTime gewünschter Startzeitpunkt des Termins
 */
public record AppointmentRequest(
        String name,
        String email,
        String topic,
        LocalDateTime startTime) {
}
