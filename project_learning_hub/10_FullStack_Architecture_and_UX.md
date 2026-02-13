# 10. Full Stack Architecture & UX (Sprint 10)

Das Finale: Wir verbinden alles zu einem professionellen Gesamtsystem.
Sprint 10 bringt **Local AI Privacy**, **Audit-Logging** und ein komplettes **Portfolio-CMS**.

## 1. Local AI Integration (Ollama) 🦙
Wir sind nicht mehr abhängig von der Cloud.
Mit [Ollama](https://ollama.ai) lassen wir LLMs (Llama 2, Mistral) *lokal* auf dem Rechner laufen.

### Die Architektur
Backend -> `http://localhost:11434` (Ollama Server)
Da Ollama eine OpenAI-kompatible API bietet, mussten wir im `ProviderFactory` nur die `base_url` ändern.
-> **Vorteil:** Keine Kosten, volle Privatsphäre.
-> **Nachteil:** Braucht starke Hardware (GPU/RAM), langsamer als Cloud.

## 2. Audit Logging (Middleware Pattern) 📊
Problem: "Wer nutzt meine KI und wie teuer ist das?"
Lösung: Wir loggen **jeden** Request.

### Der Middleware-Ansatz
Statt überall `print()` zu verteilen, fangen wir die KI-Antwort zentral ab (`ai_service.py`).
1.  Startuhrzeit messen (`perf_counter`)
2.  KI generieren lassen
3.  Enduhrzeit messen & Latenz berechnen
4.  Alles in die DB schreiben (`AuditService.log_request`)

Datenmodell (`ai_audit_logs`):
-   `provider` (openai, ollama...)
-   `timestamp`
-   `latency_ms` (Wichtig für Performance-Analysen!)

## 3. Full Stack CMS: Das Portfolio 🎨
Wir haben ein "Create-Read-Delete" System gebaut, das durch alle Schichten geht.

### Data Flow
1.  **Frontend (`Portfolio.vue`)**:
    -   Fetcht `GET /api/portfolio` (Public)
    -   Sendet `POST /api/admin/portfolio` (Admin Only) - **JWT Required!**
2.  **API Layer (`api.js`)**:
    -   Automatische Header-Injektion (`Authorization: Bearer ...`)
3.  **Backend (`PortfolioController.java`)**:
    -   `@GetMapping("/portfolio")` -> Öffentlich
    -   `@PostMapping("/admin/...")` -> Geschützt durch Security Config
4.  **Datenbank**:
    -   Speichert Titel, Beschreibung, URL, Tags.

## 4. Modern UX: Glassmorphism & Transitions ✨
Eine App muss sich gut *anfühlen*.

### Glassmorphism
Wir nutzen `backdrop-filter: blur(10px)` und halbtransparente Hintergründe (`bg-white/5`).
Das erzeugt Tiefe und sieht extrem modern aus.

### View Transitions
In Vue nutzen wir `<Transition name="fade">` um Router-Wechsel weich zu animieren.
Kein hartes Flackern mehr beim Seitenwechsel!

---
**Fazit:**
Vom Docker-Container über Java/Spring Boot bis hin zu Vue 3 und AI-Integration.
Das ist ein vollwertiger, skalierbarer Tech-Stack.
Glückwunsch zum Abschluss des Kurses! 🎓
