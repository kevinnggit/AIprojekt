package com.nspace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String status; // z.B. "CONFIRMED", "PENDING", "CANCELLED"

    public Appointment() {
    }

    public Appointment(String name, String email, String topic, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.email = email;
        this.topic = topic;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "PENDING";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
