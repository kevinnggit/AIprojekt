package com.nspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der NSPACE-Backend-Anwendung.
 *
 * <p>Diese Klasse startet den eingebetteten Servlet-Container (Tomcat) und
 * initialisiert den gesamten Spring-Anwendungskontext. Die Annotation
 * {@code @SpringBootApplication} aktiviert die automatische Konfiguration,
 * den Komponentenscan sowie die Bean-Registrierung.</p>
 */
@SpringBootApplication
public class JavaBackendApplication {

    /**
     * Hauptmethode – startet die Spring Boot-Anwendung.
     *
     * @param args optionale Kommandozeilenargumente (z. B. Profilangaben)
     */
    public static void main(String[] args) {
        SpringApplication.run(JavaBackendApplication.class, args);
    }
}
