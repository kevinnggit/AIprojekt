package com.nspace.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String name,
        String topic,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status) {
}
