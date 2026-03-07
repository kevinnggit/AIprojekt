# Sprint 11
**Motto:** "Security, Intelligence & Control"
**Ziel:** Die Plattform professionalisieren — bessere Sicherheit (Cookies), mehr KI-Kontrolle und tiefere Systemsicht für den Admin.

## Was wir angehen

### 1. Admin Sicherheits-Umbau (SEC-001)
- **Priorität:** Kritisch
- **Aufgaben:**
    - [Backend] Wechsel von Header-Token zu HttpOnly Cookies.
    - [Frontend] Route Guards einbauen (Redirect zu Login wenn kein Cookie).
    - [Frontend] Token aus LocalStorage entfernen.

### 2. KI UI-Umbau (AI-002)
- **Priorität:** Neue Anforderung
- **Aufgaben:**
    - [Frontend] Dynamisches Menü: Provider (OpenAI/DeepSeek/Ollama/gemini/claude/Mistral) → Modell → Aufgabe.
    - [Frontend] Aufgaben-Umschaltung: "Chat" vs. "Projekt-Generator" (Formulare ein-/ausblenden).
    - [Backend] Sicherstellen, dass das Python-Backend diese Parameter annimmt.
    - [Backend] Für jeden Provider einen Platzhalter für den API-Key anlegen (wo Keys gespeichert werden).

### 3. Admin-Erweiterungen: Aktivität & Portfolio (ADM-003)
- **Priorität:** Verbesserung
- **Aufgaben:**
    - [Frontend] "Dashboard"-Übersicht mit Live-Stats (Termine heute, Fehler, API-Aufrufe).
    - [Backend] Einfacher Aktivitäts-Log oder Stats-Endpunkt (`/api/admin/stats`).
    - [Portfolio] Bestehende Implementierung reviewen und aufpolieren.

### 4. Infrastruktur & HTTPS (INF-001)
- **Priorität:** Planung/Doku
- **Aufgaben:**
    - Doku schreiben: "Wie HTTPS aktivieren" (Nginx Proxy).
    - `docker-compose.yml` härtren.

## Zeitplan
- **Tag 1:** Sicherheits-Umbau (Cookie-Logik ist tricky).
- **Tag 2:** KI-Umbau (Vue-Komponenten).
- **Tag 3:** Admin-Stats und Dokumentation.

## Was am Ende fertig sein soll
- Sicheres Auth: Kein JWT mehr in LocalStorage.
- Verbessertes KI-Interface: Dropdown für Modelle.
- Admin-Stats: Eine Ansicht, die den Systemzustand zeigt.
