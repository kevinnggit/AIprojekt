# 06. Advanced Appointment Management (Sprint 6)

Willkommen zur Masterclass. Wir haben unser System von "funktioniert halt" zu "Enterprise-Ready" aufgerüstet.
Das bedeutet: Strikte Regeln, typsichere Zustände und ein echtes Admin-Dashboard.

## 1. Business Logic im Service Layer
Wo validiert man Daten?
-   **Frontend?** Ja, für User Experience (roter Rahmen). Aber unsicher (kann man manipulieren).
-   **Controller?** Nein, der ist nur der "Kellner".
-   **Service?** **JA!** Hier gehört die Geschäftslogik hin.

### `validateAppointmentTime`
Wir nutzen Java's `LocalDateTime` API, um Regeln durchzusetzen:

```java
// Rule 1: Nur Mo-Fr
if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) ...

// Rule 2: 10:00 - 15:00 Uhr
if (time.isBefore(LocalTime.of(10, 0)) ...
```
Wenn der User (oder ein Hacker) versucht, einen Termin am Sonntag zu buchen, wirft der Service eine `IllegalArgumentException`. Der Controller fängt diese und sendet 400 Bad Request.

## 2. State Management mit Enums
Früher war `status` ein String ("Pending", "pending", "Confirmed").
Das ist fehleranfällig (Tippfehler).

Lösung: **Enums** (`AppointmentStatus`).
```java
public enum AppointmentStatus {
    PENDING, CONFIRMED, CANCELLED
}
```
Jetzt gibt es nur exakt diese 3 Zustände. Der Compiler passt auf uns auf.

## 3. Role-Based Security (Der Fix)
Wir hatten ein Problem: Der Admin konnte sich einloggen, aber keine Admin-Aktionen ausführen.
Grund: Der JWT enthielt die Rolle nicht korrekt.

### Die Lösung in `CustomUserDetailsService`
Spring Security muss wissen, welche Rechte ein User hat. Wir mappen unsere Datenbank-Rolle (`ROLE_ADMIN`) auf ein Spring-Authority-Objekt:

```java
// Mapping DB-String -> Spring Security Authority
new SimpleGrantedAuthority(user.getRole())
```
Nur so versteht `@PreAuthorize` oder die `SecurityConfig`, dass dieser User wirklich Admin ist.

### Method Security
Wir schützen destruktive Aktionen massiv:
-   `PUT /api/termine/*/confirm` -> Nur ADMIN
-   `DELETE /api/termine/*` -> Nur ADMIN

## 4. Frontend Polling vs. WebSockets
Im Admin Dashboard sehen wir Termine. Wenn ein User bucht, soll er sofort erscheinen.
Es gibt zwei Wege:
1.  **WebSockets:** Der Server "pusht" Daten. (Geil, aber komplex).
2.  **Polling:** Das Frontend fragt alle 5 Sekunden: "Gibt's was Neues?".

Für unser MVP haben wir **Polling** (`setInterval`) gewählt.
*   **Vorteil:** Extrem einfach zu bauen (`loadAppointments()`).
*   **Nachteil:** Erzeugt Last auf dem Server (alle 5 Sek ein Request pro Admin).

```javascript
onMounted(() => {
    pollInterval = setInterval(loadAppointments, 5000)
})
```
Dies ist ein klassischer "Trade-Off" im Software-Design.
