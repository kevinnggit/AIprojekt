package com.nspace.repository;

import com.nspace.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Optional, da JpaRepository dies impliziert. Dient als Exception-Translator
            // für DB-Fehler.
// JpaRepository generiert zur Laufzeit Standard-Methoden wie findAll(), save(),
// deleteById().
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Hier können benutzerdefinierte Query-Methoden hinzugefügt werden
    // z.B. List<Appointment> findByEmail(String email);
    // Spring Data generiert die SQL-Query automatisch anhand des Methodennamens!
}
