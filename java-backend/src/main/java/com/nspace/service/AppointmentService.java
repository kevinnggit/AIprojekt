package com.nspace.service;

import com.nspace.dto.AppointmentRequest;
import com.nspace.dto.AppointmentResponse;
import com.nspace.model.Appointment;
import com.nspace.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // Markiert diese Klasse als "Service Bean" für Business Logic. Spring verwaltet
         // sie als Singleton.
public class AppointmentService {

    private final AppointmentRepository repository;

    // Dependency Injection: Der Service braucht das Repository, um Daten zu
    // laden/speichern.
    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public List<AppointmentResponse> getAllAppointments() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse createAppointment(AppointmentRequest request) {
        // Simple Validierung: Endzeit ist 1 Stunde nach Startzeit (Standard)
        LocalDateTime endTime = request.startTime().plusHours(1);

        Appointment appointment = new Appointment(
                request.name(),
                request.email(),
                request.topic(),
                request.startTime(),
                endTime);

        Appointment saved = repository.save(appointment);
        return mapToResponse(saved);
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
