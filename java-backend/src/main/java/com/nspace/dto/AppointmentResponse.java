package com.nspace.dto;

import java.time.LocalDateTime;
import com.nspace.model.AppointmentStatus;

/**
 * Datenobjekt (DTO) für die Antwort bei Terminabfragen.
 *
 * <p>Enthält alle für das Frontend relevanten Termindaten. Die E-Mail-Adresse
 * wird bewusst nicht übertragen, um Datenschutzanforderungen zu genügen.
 * Der Status wird als typsicheres Enum übermittelt, das im Frontend
 * als String serialisiert wird.</p>
 *
 * @param id        eindeutige Datenbank-ID des Termins
 * @param name      Name des Buchenden
 * @param topic     Thema des Termins
 * @param startTime Startzeitpunkt
 * @param endTime   Endzeitpunkt (immer Startzeit + 1 Stunde)
 * @param status    aktueller Status des Termins ({@link AppointmentStatus})
 */
public record AppointmentResponse(
        Long id,
        String name,
        String topic,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status) { // Use Enum directly
}
