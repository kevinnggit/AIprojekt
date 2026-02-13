package com.nspace.dto;

import java.time.LocalDateTime;
import com.nspace.model.AppointmentStatus;

public record AppointmentResponse(
        Long id,
        String name,
        String topic,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status) { // Use Enum directly
}
