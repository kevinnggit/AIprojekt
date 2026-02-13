package com.nspace.service;

import com.nspace.model.GlobalConfig;
import com.nspace.repository.ConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    private final ConfigRepository repository;

    public ConfigService(ConfigRepository repository) {
        this.repository = repository;
    }

    public String getValue(String key, String defaultValue) {
        return repository.findById(key)
                .map(GlobalConfig::getConfigValue)
                .orElse(defaultValue);
    }

    // ⚙️ Graceful Degradation Pattern
    // Wir versuchen, den Wert aus der DB zu laden.
    // Falls er nicht existiert (oder DB leer), nutzen wir 'defaultValue'.
    // Das verhindert Abstürze bei fehlender Konfiguration.
    public int getInt(String key, int defaultValue) {
        String val = getValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void setValue(String key, String value, String description) {
        GlobalConfig config = repository.findById(key).orElse(new GlobalConfig(key, value, description));
        config.setConfigValue(value);
        if (description != null) {
            config.setDescription(description);
        }
        repository.save(config);
    }
}
