package com.nspace.controller;

import com.nspace.dto.AppointmentRequest;
import com.nspace.dto.AppointmentResponse;
import com.nspace.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // 🌍 PUBLIC READ Config
    // Dieser Endpoint ist öffentlich (siehe SecurityConfig), damit das Frontend
    // die Limits kennt, BEVOR der User etwas eingibt.
    @GetMapping("/config")
    public ResponseEntity<Map<String, Integer>> getPublicConfig() {
        int months = configService.getInt("booking_window_months", 3);
        return ResponseEntity.ok(Map.of("booking_window_months", months));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getTermine() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createTermin(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(service.createAppointment(request));
    }

    // Admin only endpoint - secured via SecurityConfig or MethodSecurity
    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirmTermin(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmAppointment(id));
    }

    // Admin only endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTermin(@PathVariable Long id) {
        service.deleteAppointment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of("status", "active", "feature", "Appointments"));
    }

    // Exception Handler for Validation Errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
