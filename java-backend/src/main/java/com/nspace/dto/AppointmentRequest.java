package com.nspace.dto;

import java.time.LocalDateTime;

public record AppointmentRequest(
        String name,
        String email,
        String topic,
        LocalDateTime startTime) {
}
