# NSPACE – Portfolio-Webseite mit Terminverwaltung und KI-Diensten

NSPACE ist ein vollständig containerisiertes Portfolio-Projekt, das drei unabhängige Dienste hinter einem gemeinsamen Vue.js-Frontend zusammenführt. Das Java-Backend (Spring Boot 3 / Java 21 LTS) verantwortet die strukturierte Terminverwaltung samt Authentifizierung, während ein Python-Backend (FastAPI) als flexibler Adapter für verschiedene KI-Provider dient. Eine PostgreSQL-Datenbank bildet die persistente Datenschicht für beide Backends. Die Orchestrierung aller Dienste erfolgt über Docker Compose.

---

## Inhaltsverzeichnis

1. [Architekturübersicht](#architekturübersicht)
2. [Technologie-Stack](#technologie-stack)
3. [Verzeichnisstruktur](#verzeichnisstruktur)
4. [Voraussetzungen](#voraussetzungen)
5. [Konfiguration](#konfiguration)
6. [Installation und Inbetriebnahme](#installation-und-inbetriebnahme)
7. [Entwicklungsumgebung](#entwicklungsumgebung)
8. [API-Referenz](#api-referenz)
9. [Sicherheitskonzept](#sicherheitskonzept)
10. [Tests ausführen](#tests-ausführen)
11. [Projektstatus](#projektstatus)

---

## Architekturübersicht

Die Anwendung folgt einer Microservices-Architektur mit vier Diensten, die über ein internes Docker-Netzwerk (`app-network`) kommunizieren. Jeder Dienst hat eine klar abgegrenzte Zuständigkeit.

```
Benutzer
  │
  ▼
┌──────────────────────────────────┐
│  Vue 3 + Vite / NGINX            │  Frontend  (Host: Port 3000 → Container: Port 80)
└───────┬──────────────────────────┘
        │  HTTP / JSON (REST)
   ┌────▼──────────────┐    ┌─────────────────────────────┐
   │  Java Backend     │    │  Python Backend             │
   │  Spring Boot 3    │    │  FastAPI                    │
   │  Port 8081        │    │  Port 8000                  │
   │  (Auth, Termine,  │    │  (KI-Provider-Adapter,      │
   │   Portfolio)      │    │   Audit-Logging)            │
   └────┬──────────────┘    └──────────────┬──────────────┘
        │                                  │
        └─────────────────┬────────────────┘
                          ▼
              ┌─────────────────────┐
              │  PostgreSQL 15      │
              │  Port 5432          │
              │  (Shared DB-Server, │
              │   separate Schemas) │
              └─────────────────────┘
```

Beide Backends greifen auf unterschiedliche Datenbanken desselben PostgreSQL-Servers zu und verwenden separate Datenbankbenutzer, was dem Prinzip der minimalen Rechtevergabe entspricht.

### Kommunikationsfluss: Terminbuchung

1. Der Benutzer füllt das Buchungsformular im Frontend aus.
2. Das Vue-Frontend sendet `POST /api/termine` an das Java-Backend.
3. Spring Boot validiert die Eingabe, prüft Geschäftsregeln (Öffnungszeiten, Überschneidungen, Buchungsfenster) und speichert den Termin in der Datenbank.
4. Die Antwort wird als JSON zurückgegeben und die Terminliste automatisch aktualisiert.

### Kommunikationsfluss: KI-Anfrage

1. Der Benutzer gibt einen Prompt im Frontend ein und wählt optional einen KI-Provider.
2. Das Vue-Frontend sendet `POST /api/ki/infer` an das Python-Backend.
3. FastAPI delegiert die Anfrage an den gewählten Provider (OpenAI, DeepSeek, Ollama oder Mock).
4. Die Antwort wird strukturiert zurückgegeben, im Frontend angezeigt und in der Audit-Tabelle protokolliert.

---

## Technologie-Stack

| Komponente         | Technologie                              | Version       |
|--------------------|------------------------------------------|---------------|
| Frontend           | Vue 3, Vite, Pinia, Vue Router           | Vue 3.5, Vite 7.1 |
| Frontend-Laufzeit  | NGINX                                    | alpine        |
| Java-Backend       | Spring Boot, Spring Security, Spring JPA | 3.2.0         |
| Java-Laufzeit      | OpenJDK (eclipse-temurin)                | 21 LTS        |
| Python-Backend     | FastAPI, Uvicorn, SQLAlchemy, Pydantic   | FastAPI 0.104 |
| Python-Laufzeit    | Python                                   | 3.11          |
| Datenbank          | PostgreSQL                               | 15            |
| Containerisierung  | Docker, Docker Compose                   | –             |
| Build (Java)       | Maven                                    | 3.9.x         |
| Build (Frontend)   | Node.js / npm                            | Node 20       |

---

## Verzeichnisstruktur

```
AIprojekt/
├── docker/
│   └── init.sql                  # PostgreSQL-Initialisierungsskript
│                                 # (Datenbanken und Nutzer anlegen)
├── docs/                         # Projektdokumentation
│   ├── BACKLOG.md                # Produkt-Backlog
│   ├── planning/
│   │   └── STATUS.md             # Aktueller Projektstatus
│   ├── research/
│   │   └── HTTPS.md              # HTTPS-Recherche
│   ├── specs/                    # Feature-Spezifikationen (Sprint 11)
│   └── sprints/
│       └── SPRINT_11.md          # Aktuelle Sprint-Planung
├── java-backend/
│   ├── src/main/java/com/nspace/
│   │   ├── config/               # Spring-Konfiguration (SecurityConfig)
│   │   ├── controller/           # REST-Controller (Auth, Termine, Admin, Portfolio)
│   │   ├── dto/                  # Datentransferobjekte als Java Records
│   │   ├── model/                # JPA-Entitäten (User, Appointment, PortfolioItem, GlobalConfig)
│   │   ├── repository/           # Spring Data JPA Repositories
│   │   ├── security/             # JWT-Filter, CustomUserDetailsService, JwtUtil
│   │   ├── service/              # Geschäftslogik (Auth, Appointment, Portfolio, Config)
│   │   └── JavaBackendApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── src/test/                 # JUnit 5 + Mockito Unit-Tests
│   ├── Dockerfile
│   └── pom.xml
├── python-backend/
│   ├── src/
│   │   ├── models/               # Pydantic-DTOs und SQLAlchemy-Audit-Modell
│   │   ├── routers/              # FastAPI-Router (/api/ki)
│   │   ├── services/             # AIService, Provider-Klassen, AuditService
│   │   ├── config.py             # Konfiguration via Umgebungsvariablen
│   │   ├── database.py           # SQLAlchemy-Engine und Session-Factory
│   │   ├── main.py               # Anwendungs-Einstiegspunkt, CORS-Konfiguration
│   │   ├── prompts.py            # System-Prompt-Vorlagen
│   │   └── utils.py              # Hilfsfunktionen
│   ├── tests/
│   ├── Dockerfile
│   └── requirements.txt
├── vue-frontend/
│   ├── src/
│   │   ├── components/
│   │   │   └── CalendarView.vue  # Kalenderkomponente für die Terminauswahl
│   │   ├── router/
│   │   │   └── index.js          # Vue Router mit Guard für Admin-Bereich
│   │   ├── services/
│   │   │   └── api.js            # Zentrales API-Client-Modul
│   │   ├── stores/
│   │   │   └── auth.js           # Pinia-Store für Authentifizierungszustand
│   │   ├── views/                # Seiten-Komponenten
│   │   │   ├── Home.vue
│   │   │   ├── Login.vue
│   │   │   ├── TermineJava.vue
│   │   │   ├── KiPython.vue
│   │   │   ├── AdminDashboard.vue
│   │   │   ├── Profile.vue
│   │   │   └── Portfolio.vue
│   │   ├── App.vue               # Haupt-Komponente mit Navigation
│   │   └── main.js               # Einstiegspunkt, Plugin-Registrierung
│   ├── nginx.conf                # NGINX-Konfiguration (SPA-Routing)
│   ├── Dockerfile
│   └── package.json
├── logs/                         # Laufzeit-Logs (per Docker Volume eingebunden)
├── .env                          # Haupt-Konfigurationsdatei (nicht einchecken!)
├── docker-compose.yml            # Container-Orchestrierung
├── start-all.sh                  # Hilfsskript zum Starten (Linux/macOS)
├── start-all.ps1                 # Hilfsskript zum Starten (Windows)
└── README.md                     # Diese Datei
```

---

## Voraussetzungen

**Für den Betrieb mit Docker (empfohlen):**
- Docker Engine ≥ 24.0
- Docker Compose ≥ 2.20
- OpenAI API Key (nur für KI-Funktionen nötig; ohne Schlüssel läuft der MockProvider)

**Für die lokale Entwicklung ohne Docker (optional):**
- JDK 21 (mit korrekt gesetzter `JAVA_HOME`-Variable)
- Maven ≥ 3.9
- Node.js ≥ 20 / npm ≥ 10
- Python 3.11
- PostgreSQL 15 (lokal oder als einzelner Docker-Container)

---

## Konfiguration

Alle Konfigurationsparameter werden über die Datei `.env` im Projektstamm verwaltet. Docker Compose liest diese Datei beim Start automatisch ein.

| Variable             | Beschreibung                                    | Standardwert            |
|----------------------|-------------------------------------------------|-------------------------|
| `FRONTEND_PORT`      | Externer Port des Frontends                     | `3000`                  |
| `JAVA_PORT`          | Externer Port des Java-Backends                 | `8081`                  |
| `PYTHON_PORT`        | Externer Port des Python-Backends               | `8000`                  |
| `DB_PORT`            | Externer PostgreSQL-Port                        | `5432`                  |
| `POSTGRES_USER`      | PostgreSQL-Admin-Benutzer                       | –                       |
| `POSTGRES_PASSWORD`  | PostgreSQL-Admin-Passwort                       | –                       |
| `JAVA_DB_USER`       | Datenbankbenutzer für das Java-Backend          | –                       |
| `JAVA_DB_PASSWORD`   | Passwort des Java-Datenbankbenutzers            | –                       |
| `PYTHON_DB_USER`     | Datenbankbenutzer für das Python-Backend        | –                       |
| `PYTHON_DB_PASSWORD` | Passwort des Python-Datenbankbenutzers          | –                       |
| `JWT_SECRET`         | Geheimer Schlüssel für JWT-Signierung (≥256 Bit)| –                       |
| `OPENAI_API_KEY`     | OpenAI API-Schlüssel                            | –                       |
| `OPENAI_MODEL`       | Zu verwendendes OpenAI-Modell                   | `gpt-3.5-turbo`         |
| `ALLOWED_ORIGINS`    | CORS-erlaubte Ursprünge (kommagetrennt)         | `http://localhost:3000` |
| `SYSTEM_PROMPT`      | Standard-Systemprompt für den KI-Service        | –                       |

**Sicherheitshinweis:** Die `.env`-Datei enthält sensible Zugangsdaten und darf niemals in ein öffentliches Repository eingecheckt werden. Der Eintrag in `.gitignore` schützt davor. Bei produktivem Einsatz müssen alle Standardpasswörter durch kryptografisch starke, zufällig generierte Werte ersetzt werden.

---

## Installation und Inbetriebnahme

### Schritt 1: Repository klonen

```bash
git clone <repository-url>
cd AIprojekt
```

### Schritt 2: Konfiguration prüfen

Die `.env`-Datei ist bereits vorhanden. Für KI-Funktionen muss der OpenAI-Schlüssel eingetragen werden:

```ini
OPENAI_API_KEY=sk-...
```

Ohne diesen Schlüssel fällt der KI-Service automatisch auf den MockProvider zurück, der Platzhalterantworten liefert.

### Schritt 3: Anwendung starten

```bash
docker compose up -d --build
```

Der erste Start dauert einige Minuten, da alle Docker-Images gebaut und Abhängigkeiten heruntergeladen werden. Anschließend laufen alle vier Dienste im Hintergrund.

### Schritt 4: Dienste aufrufen

| Dienst                   | URL                                   |
|--------------------------|---------------------------------------|
| Frontend                 | http://localhost:3000                 |
| Java-Backend API         | http://localhost:8081/api/termine     |
| Python-Backend Swagger   | http://localhost:8000/docs            |
| PostgreSQL               | localhost:5432                        |

### Schritt 5: Anwendung stoppen

```bash
docker compose down          # Dienste stoppen, Daten bleiben erhalten
docker compose down -v       # Dienste stoppen und PostgreSQL-Volume löschen
```

---

## Entwicklungsumgebung

### Java-Backend

```bash
cd java-backend

# Vollständigen Build mit Tests durchführen
mvn clean verify

# Nur starten (erfordert laufende PostgreSQL-Instanz)
mvn spring-boot:run

# Nur Tests ausführen
mvn test
```

Den Datenbankdienst separat starten, wenn kein lokales PostgreSQL vorhanden ist:

```bash
docker compose up postgres -d
```

### Python-Backend

```bash
cd python-backend

# Abhängigkeiten installieren
pip install -r requirements.txt

# Backend mit automatischem Reload starten
uvicorn src.main:app --reload --port 8000

# Tests ausführen
pytest tests/
```

### Vue-Frontend

```bash
cd vue-frontend

# Abhängigkeiten installieren
npm install

# Entwicklungsserver starten (Hot Module Replacement)
npm run dev

# Produktions-Build erstellen
npm run build
```

---

## API-Referenz

### Java-Backend (Port 8081)

#### Authentifizierung (`/api/auth`)

| Methode | Pfad                  | Zugriffsrecht | Beschreibung                          |
|---------|-----------------------|---------------|---------------------------------------|
| POST    | `/api/auth/login`     | Öffentlich    | Anmelden, gibt JWT-Token zurück       |
| POST    | `/api/auth/register`  | Öffentlich    | Neuen Benutzer registrieren           |

#### Terminverwaltung (`/api/termine`)

| Methode | Pfad                        | Zugriffsrecht | Beschreibung                          |
|---------|-----------------------------|---------------|---------------------------------------|
| GET     | `/api/termine`              | Öffentlich    | Alle Termine abrufen                  |
| POST    | `/api/termine`              | Öffentlich    | Neuen Termin buchen                   |
| PUT     | `/api/termine/{id}/confirm` | Admin         | Termin bestätigen                     |
| DELETE  | `/api/termine/{id}`         | Admin         | Termin löschen                        |
| GET     | `/api/termine/config`       | Öffentlich    | Buchungskonfiguration abrufen         |

#### Administration (`/api/admin`)

| Methode | Pfad                  | Zugriffsrecht | Beschreibung                          |
|---------|-----------------------|---------------|---------------------------------------|
| GET     | `/api/admin/config`   | Admin         | Alle Systemkonfigurationen abrufen    |
| POST    | `/api/admin/config`   | Admin         | Konfigurationswert setzen             |
| GET     | `/api/admin/users`    | Admin         | Alle Benutzer auflisten               |
| POST    | `/api/admin/users`    | Admin         | Neuen Benutzer anlegen                |

#### Portfolio (`/api/portfolio`)

| Methode | Pfad                        | Zugriffsrecht | Beschreibung                          |
|---------|------------------------------|---------------|---------------------------------------|
| GET     | `/api/portfolio`             | Öffentlich    | Alle Portfolio-Einträge abrufen       |
| POST    | `/api/admin/portfolio`       | Admin         | Portfolio-Eintrag erstellen           |
| DELETE  | `/api/admin/portfolio/{id}`  | Admin         | Portfolio-Eintrag löschen             |

Geschützte Endpunkte erwarten den JWT im HTTP-Header:

```
Authorization: Bearer <token>
```

#### Beispiel: Termin buchen

```json
POST /api/termine
Content-Type: application/json

{
  "name": "Max Mustermann",
  "email": "max@beispiel.de",
  "topic": "Projektbesprechung",
  "startTime": "2026-04-15T10:00:00"
}
```

### Python-Backend (Port 8000)

| Methode | Pfad                     | Beschreibung                               |
|---------|--------------------------|--------------------------------------------|
| GET     | `/`                      | Service-Status                             |
| GET     | `/health`                | Health-Check                               |
| POST    | `/api/ki/infer`          | Textanalyse durch ausgewählten KI-Provider |
| POST    | `/api/ki/generate-ideas` | Projektideen zu einem Thema generieren     |

Verfügbare Provider: `openai`, `deepseek`, `ollama`, `mock`

---

## Sicherheitskonzept

### Zustandslose Authentifizierung via JWT

Das Java-Backend setzt auf zustandslose Authentifizierung über JSON Web Tokens (RFC 7519). Bei erfolgreicher Anmeldung wird ein mit HMAC-SHA256 signiertes Token mit einer Gültigkeitsdauer von zehn Stunden ausgestellt. Das Token enthält die Benutzerrolle als Claim und wird serverseitig nicht gespeichert. Der Sitzungszustand liegt vollständig beim Client.

Jede Anfrage an geschützte Endpunkte wird durch den `JwtAuthenticationFilter` verarbeitet, der das Token aus dem Authorization-Header extrahiert, die Signatur und Gültigkeit prüft und den Spring Security Context befüllt.

### Rollenbasierte Zugriffskontrolle (RBAC)

Zwei Rollen sind definiert:

- `ROLE_USER` – Standardbenutzer; darf Termine einsehen und buchen.
- `ROLE_ADMIN` – Administratorrolle; hat Zugriff auf alle Verwaltungsendpunkte.

### Passwort-Hashing

Sämtliche Passwörter werden mit BCrypt gehasht gespeichert. Im Klartext wird kein Passwort in der Datenbank abgelegt.

### Buchungsvalidierung (serverseitig)

Termineingaben werden serverseitig validiert, unabhängig von clientseitiger Fehlerbehandlung:

- Buchungen in der Vergangenheit werden abgelehnt.
- Das maximale Buchungsfenster wird dynamisch aus der Datenbank gelesen (Standard: 3 Monate).
- Nur Werktage (Montag–Freitag) sind buchbar.
- Buchungen sind nur innerhalb der Öffnungszeiten (10:00–15:00 Uhr) möglich.
- Doppelbuchungen desselben Zeitslots werden verhindert.

---

## Tests ausführen

```bash
# Java Unit-Tests (JUnit 5 + Mockito)
docker compose run --rm java-backend mvn test

# Lokal
cd java-backend && mvn test

# Python-Tests (pytest)
docker compose exec python-backend pytest tests/
```

---

## Projektstatus

Das Projekt befindet sich im Prototyp-Stadium (Pre-MVP).

| Funktion                                     | Status |
|----------------------------------------------|--------|
| Terminbuchung mit serverseitiger Validierung | ✅     |
| Terminbestätigung und -löschung durch Admin  | ✅     |
| JWT-Authentifizierung                        | ✅     |
| Rollenbasierte Zugriffskontrolle (RBAC)      | ✅     |
| KI-Proxy (OpenAI, DeepSeek, Ollama, Mock)    | ✅     |
| Audit-Logging für KI-Anfragen                | ✅     |
| Admin-Dashboard                              | ✅     |
| Portfolio-Verwaltung                         | ✅     |
| Docker-Containerisierung                     | ✅     |
| Java 21 LTS (validiert, release 21)          | ✅     |
| E-Mail-Bestätigung                           | ⏳     |
| HTTPS / TLS-Zertifikate                      | ⏳     |
| Zahlungsintegration                          | 🔲     |
