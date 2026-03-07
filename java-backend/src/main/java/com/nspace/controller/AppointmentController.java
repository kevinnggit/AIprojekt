package com.nspace.controller;

import com.nspace.dto.AppointmentRequest;
import com.nspace.dto.AppointmentResponse;
import com.nspace.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST-Controller für die Terminverwaltung (Buchung, Auflisting, Bestätigung, Löschung).
 *
 * <p>Die Zugriffskontrolle erfolgt ausschließlich über die {@code SecurityConfig}:
 * Öffentliche Aktionen (Lesen, Erstellen) sind ohne Authentifizierung erlaubt,
 * während Bestätigung und Löschung ausdrücklich {@code ROLE_ADMIN} erfordern.</p>
 */
@RestController
@RequestMapping("/api/termine")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService service;
    private final com.nspace.service.ConfigService configService;

    public AppointmentController(AppointmentService service, com.nspace.service.ConfigService configService) {
        this.service = service;
        this.configService = configService;
    }

    /**
     * Gibt die buchungsrelevante Konfiguration heraus (z. B. maximales Buchungsfenster).
     *
     * <p>Dieser Endpunkt ist öffentlich zugänglich (siehe SecurityConfig), damit das
     * Frontend die Einschränkungen kennt, bevor der Benutzer eine Eingabe macht.</p>
     *
     * @return Map mit dem Konfigurationswert {@code booking_window_months}
     */
    // Dieser Endpoint ist öffentlich (siehe SecurityConfig), damit das Frontend
    // die Limits kennt, BEVOR der User etwas eingibt.
    @GetMapping("/config")
    public ResponseEntity<Map<String, Integer>> getPublicConfig() {
        int months = configService.getInt("booking_window_months", 3);
        return ResponseEntity.ok(Map.of("booking_window_months", months));
    }

    /**
     * Gibt alle gespeicherten Termine zurück (öffentlich lesbar).
     *
     * @return Liste aller Termine als {@link AppointmentResponse}-Objekte
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getTermine() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    /**
     * Erstellt einen neuen Termin nach Validierung der Buchungsregeln.
     *
     * @param request Buchungsdaten des Nutzers (Name, E-Mail, Thema, Startzeit)
     * @return der gespeicherte Termin mit generierten Feldern (ID, Endzeit, Status)
     */
    @PostMapping
    public ResponseEntity<AppointmentResponse> createTermin(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(service.createAppointment(request));
    }

    /**
     * Bestätigt einen ausstehenden Termin – nur für Admins zugänglich.
     *
     * @param id die ID des zu bestätigenden Termins
     * @return der aktualisierte Termin mit Status {@code CONFIRMED}
     */
    // Admin only endpoint - secured via SecurityConfig or MethodSecurity
    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirmTermin(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmAppointment(id));
    }

    /**
     * Löscht einen Termin anhand seiner ID – nur für Admins zugänglich.
     *
     * @param id die ID des zu löschenden Termins
     * @return HTTP 200 ohne Rumpf
     */
    // Admin only endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTermin(@PathVariable Long id) {
        service.deleteAppointment(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Einfacher Statusendpunkt zur Überprüfung, ob das Termin-Feature aktiv ist.
     *
     * @return Map mit Statusinformationen
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of("status", "active", "feature", "Appointments"));
    }

    /**
     * Fängt Validierungsfehler aus der Serviceschicht ab und gibt eine lesbare
     * Fehlermeldung an den Client zurück (HTTP 400).
     *
     * @param e die ausgelöste {@link IllegalArgumentException}
     * @return HTTP 400 mit der Fehlermeldung als JSON-Map
     */
    // Exception Handler for Validation Errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
