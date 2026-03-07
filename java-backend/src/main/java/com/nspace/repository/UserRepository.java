package com.nspace.repository;

import com.nspace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository-Interface für den Datenbankzugriff auf {@link User}-Entitäten.
 *
 * <p>Erweitert {@link JpaRepository} und erbt dadurch Standard-CRUD-Operationen
 * (speichern, lesen, löschen). Die Methode {@code findByUsername} wird von
 * Spring Data JPA automatisch aus dem Methodennamen abgeleitet und generiert
 * die entsprechende SQL-Abfrage zur Laufzeit.</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Sucht einen Benutzer anhand seines Benutzernamens.
     *
     * <p>Wird im Authentifizierungsfluss verwendet, um zu prüfen ob ein Benutzername
     * bereits vergeben ist, oder um den Benutzer beim Login zu laden.</p>
     *
     * @param username der eindeutige Benutzername
     * @return ein {@link Optional} mit dem gefundenen Benutzer, oder leer wenn nicht vorhanden
     */
    Optional<User> findByUsername(String username);
}
