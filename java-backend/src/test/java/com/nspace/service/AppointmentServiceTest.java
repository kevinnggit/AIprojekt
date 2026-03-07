package com.nspace.service;

import com.nspace.dto.AppointmentRequest;
import com.nspace.dto.AppointmentResponse;
import com.nspace.model.Appointment;
import com.nspace.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für {@link AppointmentService}.
 *
 * <p>Testet die Serviceschicht isoliert von der Datenbank: Repository und ConfigService
 * werden mit Mockito gemockt, sodass ausschließlich die Geschäftslogik des Services
 * geprüft wird. Die Tests folgen dem AAA-Muster (Arrange, Act, Assert).</p>
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private ConfigService configService;

    // Mockito injiziert die obigen Mocks automatisch in den Service
    @InjectMocks
    private AppointmentService service;

    /**
     * Stellt sicher, dass {@code getAllAppointments()} alle Datenbankeinträge als DTOs zurückgibt.
     */
    @Test
    void getAllAppointments_ShouldReturnList() {
        // Arrange
        Appointment appt = new Appointment("Max", "test@mail.com", "Talk", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1));
        when(repository.findAll()).thenReturn(List.of(appt));

        // Act
        List<AppointmentResponse> result = service.getAllAppointments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Max", result.get(0).name());
        verify(repository, times(1)).findAll();
    }

    /**
     * Hilfsmethode: Berechnet einen validen Buchungszeitpunkt in der Zukunft.
     *
     * <p>Springt mindestens 7 Tage vor und sucht dann den nächsten Montag,
     * um die Wochentag-Validierung im Service sicher zu bestehen.
     * Uhrzeit wird auf 10:00 Uhr gesetzt (innerhalb der Öffnungszeiten).</p>
     *
     * @return ein {@link LocalDateTime}, das alle Buchungsregeln erfüllt
     */
    private LocalDateTime getValidFutureDate() {
        LocalDateTime now = LocalDateTime.now();
        // Skip ahead to ensure future
        LocalDateTime future = now.plusDays(7);
        // Find next Monday – Werktag-Regel erfüllen
        while (future.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
            future = future.plusDays(1);
        }
        return future.withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * Stellt sicher, dass {@code createAppointment()} einen Termin korrekt speichert
     * und die Felder des gespeicherten Objekts in der Antwort zurückgibt.
     */
    @Test
    void createAppointment_ShouldSaveAndReturnResponse() {
        // Arrange
        LocalDateTime start = getValidFutureDate();
        AppointmentRequest request = new AppointmentRequest("Max", "test@mail.com", "Talk", start);

        Appointment savedAppt = new Appointment("Max", "test@mail.com", "Talk", start, start.plusHours(1));

        // Konfigurationsservice gibt Buchungsfenster von 3 Monaten zurück
        when(configService.getInt(anyString(), anyInt())).thenReturn(3);
        // Slot ist noch frei – keine Doppelbuchung
        when(repository.existsByStartTime(start)).thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(savedAppt);

        // Act
        AppointmentResponse response = service.createAppointment(request);

        // Assert
        assertNotNull(response);
        assertEquals("Max", response.name());
        assertEquals(start, response.startTime());
        verify(repository, times(1)).save(any(Appointment.class));
    }
}
