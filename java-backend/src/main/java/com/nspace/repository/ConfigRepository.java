package com.nspace.repository;

import com.nspace.model.GlobalConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Interface für den Datenbankzugriff auf {@link GlobalConfig}-Einträge.
 *
 * <p>Der Primärschlüssel ist vom Typ {@link String} (der Konfigurationsschlüssel),
 * was direktes Nachschlagen per {@code findById("booking_window_months")} ermöglicht.
 * Die geerbten JPA-Methoden sind für die Konfigurationsverwaltung vollständig ausreichend.</p>
 */
public interface ConfigRepository extends JpaRepository<GlobalConfig, String> {
}
