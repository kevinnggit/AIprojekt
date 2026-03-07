package com.nspace.model;

/**
 * Enum für den Lebenszyklus-Status eines Termins.
 *
 * <p>Die Verwendung eines Enums anstelle von Freitext-Strings verhindert Tippfehler
 * und macht den Code zur Compile-Zeit typsicher. In der Datenbank wird der Name
 * des Enum-Wertes als String gespeichert (via {@code @Enumerated(EnumType.STRING)}).</p>
 *
 * <ul>
 *   <li>{@code PENDING}   – Termin wurde gebucht, wartet auf Bestätigung durch einen Admin</li>
 *   <li>{@code CONFIRMED} – Termin wurde vom Admin bestätigt</li>
 *   <li>{@code CANCELLED} – Termin wurde storniert</li>
 * </ul>
 */
// Enum für typsicheren Status.
// Im Gegensatz zu Strings ("Pending", "pending") verhindert das Tippfehler im Code.
public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
