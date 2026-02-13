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

@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final ConfigService configService;

    public AppointmentService(AppointmentRepository repository, ConfigService configService) {
        this.repository = repository;
        this.configService = configService;
    }

    public List<AppointmentResponse> getAllAppointments() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse createAppointment(AppointmentRequest request) {
        // Business Rule: Validate Time
        validateAppointmentTime(request.startTime());

        LocalDateTime endTime = request.startTime().plusHours(1);

        Appointment appointment = new Appointment(
                request.name(),
                request.email(),
                request.topic(),
                request.startTime(),
                endTime);

        // Explicitly set default status (redundant if constructor does it, but safer)
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment saved = repository.save(appointment);
        return mapToResponse(saved);
    }

    public AppointmentResponse confirmAppointment(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + id));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = repository.save(appointment);
        return mapToResponse(saved);
    }

    public void deleteAppointment(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    private void validateAppointmentTime(LocalDateTime start) {
        LocalDateTime now = LocalDateTime.now();

        // 🧠 Rule 0: Keine Buchungen in der Vergangenheit
        // Wir nutzen "now()" vom Server, nicht vom Client (Sicherheit!)
        if (start.isBefore(now)) {
            throw new IllegalArgumentException("Appointments cannot be in the past.");
        }

        // Rule 0.5: Max Booking Window (Dynamic Config)
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

        // 🔒 Rule 3: Slot Integrity Check
        // Checkt in der DB, ob schon ein Termin zu exakt dieser Zeit existiert.
        // Verhindert Doppelbuchungen auf Anwendungsebene.
        if (repository.existsByStartTime(start)) {
            throw new IllegalArgumentException("This time slot is already booked.");
        }
    }

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
