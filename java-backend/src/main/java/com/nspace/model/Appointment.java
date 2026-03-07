package com.nspace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA-Entität, die einen Terminbuchungseintrag in der Datenbank repräsentiert.
 *
 * <p>Ein Termin wird durch einen Buchenden (Name, E-Mail), ein Gesprächsthema sowie
 * einen Zeitraum (Start- und Endzeit) beschrieben. Der Status durchläuft den
 * Lebenszyklus {@code PENDING -> CONFIRMED} bzw. kann durch den Admin gelöscht werden.
 * Die Endzeit wird immer serverseitig als Startzeit + 1 Stunde berechnet.</p>
 */
@Entity // Sagt Hibernate: Erstelle/Mappe eine Datenbank-Tabelle für diese Klasse
@Table(name = "appointments") // Name der Tabelle in der DB
public class Appointment {

    @Id // Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-Increment (Serial in Postgres)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String topic;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // Der Status wird als String in der DB gespeichert (EnumType.STRING),
    // nicht als Ordinalzahl – das macht die DB menschenlesbar und ist robuster
    // bei künftigen Enum-Erweiterungen
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status; // "PENDING", "CONFIRMED", "CANCELLED"

    public Appointment() {
    }

    /**
     * Erstellt einen neuen Termin und setzt den Status automatisch auf {@code PENDING}.
     *
     * @param name      Name des Buchenden
     * @param email     E-Mail-Adresse des Buchenden
     * @param topic     Thema des Termins
     * @param startTime Startzeitpunkt
     * @param endTime   Endzeitpunkt
     */
    public Appointment(String name, String email, String topic, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.email = email;
        this.topic = topic;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AppointmentStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
