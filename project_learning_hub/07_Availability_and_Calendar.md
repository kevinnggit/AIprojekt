# 07. Availability & Calendar (Sprint 7)

Willkommen im visuellen Zeitalter.
Wir haben das Formular `Datum: [_____]` durch einen interaktiven Kalender ersetzt.
Das ist nicht nur "hübsch", sondern elementar für die User Experience (UX).

## 1. Frontend Logic: Smart vs. Dumb Components
Wir nutzen ein wichtiges Pattern in Vue: **Container vs. Presentational Components**.

### A. The "Dumb" Component (`CalendarView.vue`)
Diese Komponente weiß *nichts* über API-Calls oder Datenbanken.
Sie hat nur Inputs (Props) und Outputs (Events).

-   **Input (`props.bookedAppointments`):**
    Sie bekommt eine Liste von Terminen. Ob die aus Java, Python oder einer Mock-Datei kommen, ist ihr egal.
-   **Output (`emit('slot-selected')`):**
    Wenn der User klickt, schreit sie nur "Hier wurde geklickt!". Sie bucht nicht selbst.

### B. The "Smart" Component (`TermineJava.vue`)
Das ist der Manager.
-   Lädt Daten: `api.termine.getAll()`
-   Füttert den Kalender: `<CalendarView :bookedAppointments="termine" />`
-   Handelt den Klick: "Aha, User will 14:00 Uhr. Ich lade das ins Formular."

**Warum?**
Wir können den `CalendarView` wiederverwenden! Z.B. für das Admin-Dashboard oder das Python-Backend.

## 2. Visual Feedback Strategy
Ein User soll nicht raten müssen, ob ein Termin frei ist.

### Client-Side Filtering
Wir prüfen direkt im Browser:
```javascript
const isBooked = (day, hour) => {
    // Array.some() prüft, ob IRGENDEIN Termin auf diesen Slot fällt.
    return props.bookedAppointments.some(app => app.startTime === thisSlot);
}
```
Das ist performant genug für < 1000 Termine.
Buttons erhalten Klassen: `.booked` (rot), `.past` (grau), `.available` (grün).
UX Rule: **"Don't let the user click errors."** Wir disablen den Button `disabled="true"`.

## 3. Backend Integrity Rules (The Real Gatekeeper)
Frontend-Checks sind nett, aber **unsicher** (man kann sie im Browser manipulieren).
Das Backend (`AppointmentService`) ist die letzte Instanz.

### A. The "Exists" Check
```java
if (repository.existsByStartTime(start)) {
    throw new Exception("Slot booked");
}
```
Das verhindert (fast) alle Doppeltbuchungen.
*Profi-Tip:* Ganz sicher wäre nur ein `UNIQUE CONSTRAINT` in der Datenbank.

### B. Date Math im Backend
Wir vertrauen nicht der Uhrzeit des User-PCs.
```java
LocalDateTime now = LocalDateTime.now(); // Serverzeit!
if (start.isBefore(now)) ...
```
So verhindern wir, dass jemand seine Systemuhr zurückstellt, um vergangene Termine zu buchen.
