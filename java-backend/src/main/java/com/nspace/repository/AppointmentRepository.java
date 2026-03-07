package com.nspace.repository;

import com.nspace.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository-Interface für den Datenbankzugriff auf {@link Appointment}-Entitäten.
 *
 * <p>Stellt Standard-CRUD-Operationen bereit, die {@link JpaRepository} automatisch
 * implementiert. Die zusätzliche Methode {@code existsByStartTime} dient der
 * Doppelbuchungsprüfung auf Anwendungsebene.</p>
 */
@Repository // Optional, da JpaRepository dies impliziert. Dient als Exception-Translator
            // für DB-Fehler.
// JpaRepository generiert zur Laufzeit Standard-Methoden wie findAll(), save(),
// deleteById().
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Prüft, ob bereits ein Termin mit dem angegebenen Startzeitpunkt existiert.
     *
     * <p>Spring Data generiert daraus automatisch:
     * {@code SELECT COUNT(*) > 0 FROM appointments WHERE start_time = ?}
     * Wird in der Buchungsvalidierung verwendet, um Doppelbuchungen zu verhindern.</p>
     *
     * @param startTime der zu prüfende Startzeitpunkt
     * @return {@code true} wenn eine Buchung mit diesem Zeitpunkt bereits existiert
     */
    // Spring Data Magic: "existsBy..." generiert automatisch die SQL-Abfrage
    boolean existsByStartTime(java.time.LocalDateTime startTime);
    // Hier können benutzerdefinierte Query-Methoden hinzugefügt werden
    // z.B. List<Appointment> findByEmail(String email);
    // Spring Data generiert die SQL-Query automatisch anhand des Methodennamens!
}
