# E-Mail-Bestätigung für Terminbuchungen

**Feature:** USER-001
**Sprint:** 08

## Ziel

Wenn ein Nutzer sich einen Termin bucht, soll er direkt eine E-Mail-Bestätigung bekommen. So hat er alle Details schriftlich und das System wirkt nicht kaputt.

## Was die E-Mail enthalten soll

- "Hallo [Name],"
- "Dein Termin am [Datum] um [Uhrzeit] wurde vorgemerkt."
- "Wir melden uns bald."

Wenn der E-Mail-Versand fehlschlägt, soll der Termin trotzdem in der Datenbank gespeichert werden. Der Fehler wird geloggt, der Nutzer sieht im schlimmsten Fall nichts davon — das ist besser als ein gebuchter Termin, der stillschweigend verschwindet.

## Umsetzung (Backend Java)

1. Abhängigkeit `spring-boot-starter-mail` in `pom.xml` eintragen.
2. SMTP-Einstellungen in `application.properties` über Umgebungsvariablen konfigurieren.
3. In `docker-compose.yml` einen `mailhog/mailhog`-Container hinzufügen (Port 1025 für SMTP, 8025 für die Web-UI).
4. `EmailService.java` als `@Service` erstellen:
    ```java
    void sendConfirmation(String to, String name, LocalDateTime date);
    ```
5. In `AppointmentService.createAppointment` den E-Mail-Versand aufrufen.

**Hinweis zur Async-Ausführung:** Mit `@Async` kann der Versand im Hintergrund laufen, damit der Nutzer nicht auf die Antwort des SMTP-Servers wartet.

## SMTP-Konfiguration

- **Entwicklung:** MailHog läuft als Docker-Container und fängt alle E-Mails ab. Nichts wird wirklich versendet, aber man kann alles unter `localhost:8025` im Browser einsehen.
- **Produktion:** Google SMTP oder SendGrid über Umgebungsvariablen.

## Testen

- Docker Compose starten, MailHog sollte mit hochkommen.
- Termin buchen.
- Im Browser `localhost:8025` öffnen — die Bestätigungsmail muss dort erscheinen.
- Der Termin muss parallel in der Datenbank stehen.
