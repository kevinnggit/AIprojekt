package com.nspace.controller;

import com.nspace.dto.AppointmentRequest;
import com.nspace.dto.AppointmentResponse;
import com.nspace.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // @RestController: Kombiniert @Controller und @ResponseBody. Rückgabewerte
                // werden automatisch als JSON serialisiert.
@RequestMapping("/api/termine") // Basis-URL für alle Endpunkte in dieser Klasse
@CrossOrigin(origins = "*") // In Produktion einschränken! Erlaubt Zugriff von anderen Domains (z.B.
                            // Frontend auf Port 3000)
public class AppointmentController {

    private final AppointmentService service;

    // Dependency Injection: Spring injeziert hier automatisch die Instanz des
    // AppointmentService
    // Wir nutzen Constructor-Injection (die sicherste Variante), kein @Autowired am
    // Feld nötig.
    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getTermine() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createTermin(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(service.createAppointment(request));
    }

    // Health check für diesen spezifischen Controller (optional)
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of("status", "active", "feature", "Appointments"));
    }
}
