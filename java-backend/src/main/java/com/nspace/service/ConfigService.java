package com.nspace.service;

import com.nspace.model.GlobalConfig;
import com.nspace.repository.ConfigRepository;
import org.springframework.stereotype.Service;

/**
 * Serviceklasse für den Zugriff auf globale Anwendungskonfigurationen.
 *
 * <p>Ermöglicht das Lesen und Schreiben von Schlüssel-Wert-Konfigurationspaaren
 * aus der Datenbank. Implementiert das "Graceful Degradation"-Muster: Wenn ein
 * Konfigurationswert nicht in der DB existiert, wird ein sinnvoller Standardwert
 * verwendet, anstatt die Anwendung zum Absturz zu bringen.</p>
 */
@Service
public class ConfigService {

    private final ConfigRepository repository;

    public ConfigService(ConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * Liest einen Konfigurationswert als String aus der Datenbank.
     *
     * @param key          der Konfigurationsschlüssel
     * @param defaultValue Fallback-Wert wenn der Schlüssel nicht existiert
     * @return den gespeicherten Wert oder {@code defaultValue}
     */
    public String getValue(String key, String defaultValue) {
        return repository.findById(key)
                .map(GlobalConfig::getConfigValue)
                .orElse(defaultValue);
    }

    /**
     * Liest einen Konfigurationswert als Integer aus der Datenbank.
     *
     * <p>Graceful Degradation: Wenn der Wert nicht in der DB existiert oder nicht
     * als Ganzzahl parsebar ist, wird {@code defaultValue} zurückgegeben.
     * Das verhindert Abstürze bei fehlender oder korrupter Konfiguration.</p>
     *
     * @param key          der Konfigurationsschlüssel
     * @param defaultValue Fallback-Wert bei fehlendem oder ungültigem Eintrag
     * @return den Konfigurationswert als {@code int} oder {@code defaultValue}
     */
    // Graceful Degradation Pattern
    // Wir versuchen, den Wert aus der DB zu laden.
    // Falls er nicht existiert (oder DB leer), nutzen wir 'defaultValue'.
    // Das verhindert Abstürze bei fehlender Konfiguration.
    public int getInt(String key, int defaultValue) {
        String val = getValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            // Konfigurationswert enthält keinen gültigen Integer – Fallback verwenden
            return defaultValue;
        }
    }

    /**
     * Erstellt oder aktualisiert einen Konfigurationseintrag (Upsert-Verhalten).
     *
     * <p>Falls der Schlüssel bereits existiert, wird nur der Wert aktualisiert.
     * Die Beschreibung wird nur überschrieben wenn sie im Aufruf mitgegeben wurde.</p>
     *
     * @param key         der Konfigurationsschlüssel (Primärschlüssel in der DB)
     * @param value       der neue Wert
     * @param description optionale Beschreibung des Konfigurationsparameters
     */
    public void setValue(String key, String value, String description) {
        // Bestehenden Eintrag laden oder neuen erstellen (Upsert)
        GlobalConfig config = repository.findById(key).orElse(new GlobalConfig(key, value, description));
        config.setConfigValue(value);
        if (description != null) {
            config.setDescription(description);
        }
        repository.save(config);
    }
}
