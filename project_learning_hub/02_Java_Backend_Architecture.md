# 02. Java Backend Architektur: Das Rückgrat (Sprint 2)

Willkommen im Maschinenraum. Wir schauen uns an, wie wir im Java Backend effizienten, wartbaren Code schreiben. Wir nutzen **Spring Boot 3** auf **Java 21 LTS** mit einer strikten **Schichten-Architektur (Layered Architecture)**.

## 1. Das "Layered Architecture" Prinzip

Stellen Sie sich unser Backend wie ein Restaurant vor:

1.  **Controller (Der Kellner):** Nimmt die Bestellung entgegen, versteht "HTTP", validiert grob und gibt sie weiter. Er kocht nicht!
2.  **Service (Der Koch):** Hier passiert die Magie. Die Geschäftslogik. Er bekommt die Zutaten vom Repository und verarbeitet sie.
3.  **Repository (Der Lieferant):** Holt die Rohdaten aus dem Lager (Datenbank). Er weiß nicht, was gekocht wird.
4.  **Database (Das Lager):** Hier liegen die Daten.

### Flow-Diagramm

```mermaid
graph LR
    Request(HTTP Request) --> Controller[Controller\n(Interface)]
    Controller --> Service[Service\n(Business Logic)]
    Service --> Repo[Repository\n(Data Access)]
    Repo --> DB[(PostgreSQL)]
```

## 2. Praxis-Beispiel: "Termine"

Wir haben diese Struktur in unserem `Appointment`-Feature implementiert.

### A. Der Kellner: `AppointmentController.java`

```java
@RestController               // Sagt Spring: "Ich bin ein Web-Endpunkt"
@RequestMapping("/api/termine") // Ich höre auf diese URL
public class AppointmentController { ... }
```
-   **Aufgabe:** Nimmt JSON entgegen, wandelt es in Java-Objekte (DTOs) um.
-   **Wichtig:** Hier steht KEINE Logik (z.B. "Ist der Zeitslot noch frei?"). Das delegieren wir.

### B. Der Koch: `AppointmentService.java`

```java
@Service // Sagt Spring: "Hier steckt Logik drin"
public class AppointmentService {
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        // Logik 1: Vergangene Termine verbieten
        if (request.startTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("...");
        }
        // Logik 2: Endzeit berechnen (+1 Stunde)
        LocalDateTime endTime = request.startTime().plusHours(1);
        ...
    }
}
```
-   **Aufgabe:** Berechnungen, Entscheidungen, Transformationen, Validierungen.

### C. Der Lieferant: `AppointmentRepository.java`

```java
@Repository // Sagt Spring: "Ich rede mit der Datenbank"
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByStartTime(LocalDateTime startTime);
}
```
-   **Die Magie:** Wir schreiben hier fast kein SQL. Spring Data JPA generiert `findAll()`, `save()`, `delete()` automatisch zur Laufzeit.
-   Die eigene Methode `existsByStartTime` wird ebenfalls automatisch in eine SQL-Abfrage übersetzt — nur durch den Methodennamen!

### D. Das Rezept: `Appointment.java` (Entity)

```java
@Entity // Sagt Spring: "Ich bin eine Datenbank-Tabelle"
@Table(name = "appointments")
public class Appointment { ... }
```
-   Dies definiert das Schema unserer Tabelle `appointments` in PostgreSQL.

## 3. Dependency Injection (Das `@Autowired` Prinzip)

Wie kommen diese Teile zusammen? Wir benutzen `new AppointmentController()` nirgendwo manuell.
**Spring Boot** übernimmt das ("Inversion of Control").

```java
// Constructor Injection (Best Practice seit Spring Boot 3)
public AppointmentController(AppointmentService service) {
    this.service = service;
}
```
Spring sieht beim Start: "Aha, der Controller braucht einen Service. Ich habe einen Service. Hier hast du ihn."
Das macht den Code testbar und modular.

## 4. Java Records: Moderne DTOs ohne Boilerplate

Unser Projekt nutzt eine Besonderheit aus **Java 16+**, die in Java 21 LTS ihren festen Platz gefunden hat: **Records**.

Ein DTO (Data Transfer Object) ist ein einfaches Datenhaltungsobjekt, das Informationen zwischen Schichten transportiert. Früher sah das so aus:

```java
// Alt (Java < 16): viel Boilerplate-Code
public class LoginRequest {
    private String username;
    private String password;
    public LoginRequest(String username, String password) { ... }
    public String getUsername() { return username; }
    // ... Getter, Setter, equals(), hashCode(), toString()
}
```

Mit Java Records schreiben wir das in einer Zeile:

```java
// Modern (Java 21): kompakt, unveränderlich, selbsterklärend
public record LoginRequest(String username, String password) {}
```

Der Compiler generiert Konstruktor, Getter, `equals()`, `hashCode()` und `toString()` automatisch.
Records sind zudem **unveränderlich** (`immutable`), was Thread-Sicherheit garantiert — ideal für DTOs.

In unserem Projekt nutzen alle DTOs dieses Pattern:
- `LoginRequest`, `LoginResponse`
- `RegisterRequest`
- `AppointmentRequest`, `AppointmentResponse`

## 5. Warum Java 21 LTS?

Spring Boot 3.2.0 unterstützt Java 21 vollständig. Wir kompilieren explizit mit `--release 21`:

```xml
<!-- pom.xml: Spring Boot liest diese Eigenschaft für den Compiler -->
<java.version>21</java.version>
```

Java 21 bringt unter anderem:
- **Records** (seit Java 16, nun stabil)
- **Pattern Matching** für `instanceof`
- **Virtual Threads** (Project Loom) für hochperformante, nebenläufige Anwendungen
- **Sequenced Collections** im Collections Framework
