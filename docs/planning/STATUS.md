# Projektstatus
**Stand:** 11.01.2026

## Zusammenfassung

Die Anwendung ist ein funktionaler Prototyp (Pre-MVP).
Die Kernmechaniken (Termine buchen, KI-Spielwiese) laufen technisch stabil.
Was noch fehlt, ist die Logik, die aus dem Code ein echtes Produkt macht — Bezahlung, E-Mail-Kommunikation, Nutzerbindung.

## Funktionsübersicht

| Bereich | Stand | Einschätzung |
| :--- | :---: | :--- |
| **Terminbuchung** | teilweise | Backend und DB stehen, aber UX noch roh — keine Bestätigungsmail, keine Nutzerkonten. |
| **Kalender UI** | gut | Visuelles Feedback ist da (Sprint 7). |
| **KI Services** | teilweise | Läuft, aber die Integration ist noch lose. Welchen konkreten Mehrwert verkauft das? |
| **Admin Dashboard** | gut | Solide Basis (CRUD, Config). Reicht für MVP. |
| **Nutzersystem** | offen | Login da, aber keine Registrierung für Endkunden. |
| **Bezahlung** | fehlt | Kein Stripe, kein PayPal. Wichtig für Monetarisierung. |

## Was fehlt (die kritischen Lücken)

1. **Der Loop ist offen:** Ein Nutzer bucht einen Termin, bekommt aber keine Bestätigung per E-Mail. Wenn er vergisst, wann der Termin ist, hat er keine Möglichkeit nachzuschlagen.
2. **Monetarisierung:** Aktuell ist alles kostenlos. Wenn Geld verdient werden soll, brauchen wir Stripe/PayPal bevor Termine bestätigt werden.
3. **Content:** Es ist ein "Portfolio", aber der Fokus liegt sehr stark auf Tech-Demos. Die persönliche Geschichte des Inhabers fehlt auf der Startseite.

## Empfehlung für die nächsten Sprints

1. **Prio A: E-Mail-Service.** Ohne Bestätigungsmail wirkt das System kaputt.
2. **Prio B: Zahlungsintegration.** Wenn das Ziel Einnahmen sind.
3. **Prio C: Content und Story.** Die Startseite muss überzeugen, nicht nur Buttons zeigen.
