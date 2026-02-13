package com.nspace.model;

// Enum für typsicheren Status.
// Im Gegensatz zu Strings ("Pending", "pending") verhindert das Tippfehler im Code.
public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
