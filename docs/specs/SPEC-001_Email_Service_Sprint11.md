# Specification: Transactional Email Service

**Feature ID:** USER-001
**Sprint:** 08

## User Story
Als Nutzer möchte ich eine Email-Bestätigung erhalten, wenn ich einen Termin gebucht habe, damit ich alle Details schriftlich habe.

## Requirements

### 1. Functional Requirements
- **Trigger:** Nach erfolgreichem `POST /api/termine`.
- **Inhalt:**
    - "Hallo [Name],"
    - "Dein Termin am [Datum] um [Uhrzeit] wurde vorgemerkt."
    - "Wir melden uns bald."
- **Failure Handling:** Wenn Email-Versand scheitert, soll der Termin *trotzdem* gebucht sein (Async oder log warning). User bekommt im UI "Termin gebucht (Email konnte nicht gesendet werden)" oder wir ignorieren es silent.

### 2. Technical Requirements
- **Technology:** Java Spring Boot (`spring-boot-starter-mail`).
- **SMTP Server:**
    - Dev: `MailHog` (Docker Container) -> Fängt Emails ab, zeigt sie im Browser.
    - Prod: Google SMTP oder SendGrid (via ENV Variables).
- **Architecture:**
    - `EmailService` Class (@Service).
    - `JavaMailSender` injection.
    - Async Execution (Optional: `@Async` damit User nicht wartet).

## Technical Implementation Steps

### Backend (Java)
1.  [Dependency] Add `spring-boot-starter-mail` to `pom.xml`.
2.  [Config] Add SMTP properties to `application.properties` (use Env Vars).
3.  [Docker] Add `mailhog/mailhog` to `docker-compose.yml` (Port 1025 for SMTP, 8025 for UI).
4.  [Code] Create `EmailService.java`:
    ```java
    void sendConfirmation(String to, String name, LocalDateTime date);
    ```
5.  [Integration] Call `emailService.sendConfirmation(...)` in `AppointmentService.createAppointment`.

## Acceptance Criteria
- [ ] Docker Compose startet MailHog.
- [ ] Nach Buchung sehe ich im MailHog UI (localhost:8025) die Email.
- [ ] Termin ist in DB gespeichert.
