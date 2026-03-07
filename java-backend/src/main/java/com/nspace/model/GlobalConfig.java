package com.nspace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA-Entität für einen globalen Konfigurationseintrag.
 *
 * <p>Ermöglicht die dynamische Verwaltung von Anwendungsparametern zur Laufzeit,
 * ohne dass ein Neustart der Anwendung erforderlich ist. Der Primärschlüssel ist
 * der {@code configKey}, sodass jeder Schlüssel genau einen Wert besitzen kann.
 * Beispiel: Schlüssel {@code booking_window_months} mit Wert {@code 3}.</p>
 */
@Entity
@Table(name = "global_config")
public class GlobalConfig {

    // String als Primärschlüssel – der Schlüsselname ist eindeutig und selbsterklärend
    @Id
    private String configKey;
    private String configValue;
    private String description;

    public GlobalConfig() {
    }

    public GlobalConfig(String configKey, String configValue, String description) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
