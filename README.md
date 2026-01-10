# NSPACE - Portfolio Project

Eine moderne Portfolio-Webseite mit Terminbuchung (Java) und KI-Funktionen (Python), orchestriert mit Docker.

## Architektur
- **Frontend**: Vue 3 + Vite (Port 3000)
- **Backend 1**: Java Spring Boot (Port 8081) - Terminverwaltung
- **Backend 2**: Python FastAPI (Port 8000) - KI Services
- **Datenbank**: PostgreSQL (Port 5432)

## Voraussetzungen
- Docker & Docker Compose
- OpenAI API Key (in `.env`)

## Installation & Start

1.  **Repository klonen**:
    ```bash
    git clone <repo-url>
    cd AIPROJEKT
    ```

2.  **Konfiguration**:
    Erstelle eine `.env` Datei im Hauptverzeichnis (siehe `.env.example` falls vorhanden) oder nutze die vorhandene.
    Wichtig: Füge deinen OpenAI Key hinzu:
    ```ini
    OPENAI_API_KEY=sk-...
    ```

3.  **Starten**:
    ```bash
    docker compose up -d --build
    ```

4.  **Zugriff**:
    - **Frontend**: [http://localhost:3000](http://localhost:3000)
    - **API Doku (Python)**: [http://localhost:8000/docs](http://localhost:8000/docs)
    - **API (Java)**: [http://localhost:8081/api/termine](http://localhost:8081/api/termine)

## Features (Aktuell)
- **Termine**: Erstellen und Auflisten von Terminen (gespeichert in PostgreSQL).
- **KI**: Text-Analyse und Projektideen-Generator (via OpenAI).

## Entwicklung
- **Java Tests**: `docker compose run java-backend mvn test`
- **Python Tests**: `docker compose exec python-backend pytest`