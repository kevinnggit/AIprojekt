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

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @InjectMocks
    private AppointmentService service;

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

    @Test
    void createAppointment_ShouldSaveAndReturnResponse() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        AppointmentRequest request = new AppointmentRequest("Max", "test@mail.com", "Talk", start);

        Appointment savedAppt = new Appointment("Max", "test@mail.com", "Talk", start, start.plusHours(1));
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
