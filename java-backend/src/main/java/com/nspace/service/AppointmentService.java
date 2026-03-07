package com.nspace.service;

import com.nspace.dto.AppointmentRequest;
import com.nspace.dto.AppointmentResponse;
import com.nspace.model.Appointment;
import com.nspace.model.AppointmentStatus;
import com.nspace.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviceklasse für die gesamte Terminverwaltungslogik.
 *
 * <p>Enthält alle Buchungsregeln (Werktage, Öffnungszeiten, Buchungsfenster,
 * Doppelbuchungsschutz) sowie die CRUD-Operationen für Termine. Die Validierung
 * erfolgt ausschließlich serverseitig, um Manipulationen über den Client auszuschließen.</p>
 */
@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final ConfigService configService;

    public AppointmentService(AppointmentRepository repository, ConfigService configService) {
        this.repository = repository;
        this.configService = configService;
    }

    /**
     * Gibt alle vorhandenen Termine als DTOs zurück.
     *
     * @return Liste aller Termine als {@link AppointmentResponse}
     */
    public List<AppointmentResponse> getAllAppointments() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Erstellt einen neuen Termin nach erfolgreicher Validierung.
     *
     * <p>Die Endzeit wird serverseitig berechnet (Startzeit + 1 Stunde).
     * Der initiale Status ist immer {@code PENDING}.</p>
     *
     * @param request die Buchungsanfrage mit Nutzerdaten und gewünschter Startzeit
     * @return der gespeicherte Termin als {@link AppointmentResponse}
     * @throws IllegalArgumentException wenn eine Buchungsregel verletzt wird
     */
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        // Business Rule: Validate Time
        validateAppointmentTime(request.startTime());

        // Terminlänge ist fix: 1 Stunde
        LocalDateTime endTime = request.startTime().plusHours(1);

        Appointment appointment = new Appointment(
                request.name(),
                request.email(),
                request.topic(),
                request.startTime(),
                endTime);

        // Explizit setzen (redundant zum Konstruktor, aber verdeutlicht die Absicht)
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment saved = repository.save(appointment);
        return mapToResponse(saved);
    }

    /**
     * Bestätigt einen ausstehenden Termin durch einen Administrator.
     *
     * @param id die ID des zu bestätigenden Termins
     * @return der aktualisierte Termin mit Status {@code CONFIRMED}
     * @throws IllegalArgumentException wenn kein Termin mit dieser ID gefunden wurde
     */
    public AppointmentResponse confirmAppointment(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + id));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = repository.save(appointment);
        return mapToResponse(saved);
    }

    /**
     * Löscht einen Termin endgültig aus der Datenbank.
     *
     * @param id die ID des zu löschenden Termins
     * @throws IllegalArgumentException wenn kein Termin mit dieser ID existiert
     */
    public void deleteAppointment(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Validiert einen Buchungszeitstempel gegen alle definierten Geschäftsregeln.
     *
     * <p>Geprüft wird: kein Termin in der Vergangenheit, maximales Buchungsfenster
     * (dynamisch aus der DB), nur Werktage (Mo-Fr), Öffnungszeiten (10-15 Uhr)
     * sowie Abwesenheit einer Doppelbuchung.</p>
     *
     * @param start der gewünschte Startzeitpunkt
     * @throws IllegalArgumentException bei Verletzung einer Buchungsregel
     */
    private void validateAppointmentTime(LocalDateTime start) {
        LocalDateTime now = LocalDateTime.now();

        // Regel 0: Keine Buchungen in der Vergangenheit
        // Serverzeit wird verwendet, nicht die des Clients (Sicherheit!)
        if (start.isBefore(now)) {
            throw new IllegalArgumentException("Appointments cannot be in the past.");
        }

        // Rule 0.5: Max Booking Window (Dynamic Config)
        // Buchungsfenster wird dynamisch aus der DB geladen – Änderung ohne Neustart möglich
        int months = configService.getInt("booking_window_months", 3);
        if (start.isAfter(now.plusMonths(months))) {
            throw new IllegalArgumentException(
                    "Appointments can only be booked up to " + months + " months in advance.");
        }

        // Business Rule 1: Nur Werktags (Mo-Fr)
        DayOfWeek day = start.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Appointments are only available form Monday to Friday.");
        }

        // Business Rule 2: Öffnungszeiten (10:00 - 15:00)
        LocalTime time = start.toLocalTime();
        if (time.isBefore(LocalTime.of(10, 0)) || time.isAfter(LocalTime.of(15, 0))) {
            throw new IllegalArgumentException("Appointments are only available between 10:00 and 15:00.");
        }

        // Regel 3: Slot-Integritätsprüfung
        // Verhindert Doppelbuchungen auf Anwendungsebene durch Datenbankabfrage
        if (repository.existsByStartTime(start)) {
            throw new IllegalArgumentException("This time slot is already booked.");
        }
    }

    /**
     * Konvertiert eine {@link Appointment}-Entität in das DTO {@link AppointmentResponse}.
     * Die E-Mail-Adresse wird bewusst nicht in die Antwort übernommen (Datenschutz).
     *
     * @param appointment die zu konvertierende Entität
     * @return das zugehörige Response-DTO
     */
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getName(),
                appointment.getTopic(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus());
    }
}
