# 02. Java Backend Architektur: Das Rückgrat (Sprint 2)

Willkommen im Maschinenraum. Wir schauen uns an, wie wir im Java Backend effizienten, wartbaren Code schreiben. Wir nutzen **Spring Boot** mit einer strikten **Schichten-Architektur (Layered Architecture)**.

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
    Repo --> DB[(Database)]
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
-   **Wichtig:** Hier steht KEINE Logik (z.B. "Darf der User das?"). Das delegieren wir.

### B. Der Koch: `AppointmentService.java`

```java
@Service // Sagt Spring: "Hier steckt Logik drin"
public class AppointmentService {
    public AppointmentResponse createAppointment(...) {
        // Logik: Endzeit berechnen (+1 Stunde)
        LocalDateTime endTime = request.startTime().plusHours(1);
        ...
    }
}
```
-   **Aufgabe:** Berechnungen, Entscheidungen, Transformationen.

### C. Der Lieferant: `AppointmentRepository.java`

```java
@Repository // Sagt Spring: "Ich rede mit der Datenbank"
public interface AppointmentRepository extends JpaRepository<Appointment, Long> { }
```
-   **Die Magie:** Wir schreiben hier KEIN SQL. Spring Generate ("Magic") generiert `findAll()`, `save()`, `delete()` automatisch zur Laufzeit für uns.

### D. Das Rezept: `Appointment.java` (Entity)

```java
@Entity // Sagt Spring: "Ich bin eine Datenbank-Tabelle"
@Table(name = "appointments")
public class Appointment { ... }
```
-   Dies definiert das Schema unserer Tabelle `appointments` in Postgres.

## 3. Dependency Injection (Das `@Autowired` Prinzip)

Wie kommen diese Teile zusammen? Wir benutzen `new AppointmentController()` nirgendwo manuell.
**Spring Boot** übernimmt das ("Inversion of Control").

```java
// Constructor Injection (Best Practice)
public AppointmentController(AppointmentService service) {
    this.service = service;
}
```
Spring sieht beim Start: "Aha, der Controller braucht einen Service. Ich habe einen Service. Hier hast du ihn."
Das macht den Code testbar und modular.
