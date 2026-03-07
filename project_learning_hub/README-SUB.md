# NSPACE Portfolio Projekt — Detaillierter Technischer Bericht

## Projektübersicht

Das NSPACE Portfolio Projekt ist eine modern aufgebaute, vollständig containerisierte Webanwendung, die aus drei eigenständigen Diensten besteht: einem Vue.js-Frontend, einem Java-Spring-Boot-Backend und einem Python-FastAPI-Backend. Die Anwendung dient als professionelles Portfolio mit interaktivem Terminbuchungssystem, rollenbasierter Administrationsoberfläche, einem Portfolio-CMS und einer KI-gestützten Textgenerierung über mehrere Anbieter hinweg.

Technisch demonstriert das Projekt aktuelle Enterprise-Praktiken: stateless JWT-Authentifizierung, role-based access control (RBAC), das Factory-Entwurfsmuster für austauschbare KI-Provider, vollständiges Audit-Logging und eine datenbankgesteuerte Konfigurationsverwaltung.

---

## Architektur und Systemdesign

### Gesamtarchitektur

Das Projekt folgt einer Microservices-Architektur, bei der jeder Dienst in einem eigenen Docker-Container läuft. Die Kommunikation zwischen den Diensten erfolgt ausschließlich über definierte REST-Schnittstellen innerhalb eines privaten Docker-Netzwerks. Das Frontend wird über NGINX als statisches Asset ausgeliefert und kommuniziert per HTTP mit beiden Backends.

**Relevante Dateien:**
- Netzwerk-Konfiguration: `docker-compose.yml`
- NGINX Reverse-Proxy-Konfiguration: `vue-frontend/nginx.conf`

### Container-Orchestrierung

Docker Compose verwaltet alle vier Dienste: das Vue.js-Frontend, das Java-Backend, das Python-Backend und die PostgreSQL-Datenbank. Die `docker-compose.yml` definiert Abhängigkeiten, Portzuordnungen, Netzwerkkonfigurationen und Umgebungsvariablen. Das Frontend wartet auf die Verfügbarkeit beider Backend-Dienste, bevor es initialisiert wird.

### Netzwerkarchitektur

Alle Container sind dem Netzwerk `app-network` zugeordnet. Docker löst Containernamen automatisch als DNS-Hostnamen auf, sodass die Backends untereinander und mit der Datenbank über Namen wie `java-backend:8080` oder `postgres:5432` erreichbar sind. Externe Clients (Browser) greifen ausschließlich über die gemappten Ports des Hosts zu.

**Port-Übersicht:**

| Dienst | Interner Port | Externer Port (Host) |
| :--- | :--- | :--- |
| vue-frontend | 80 (NGINX) | 3000 |
| java-backend | 8080 | 8080 |
| python-backend | 8000 | 8000 |
| postgres | 5432 | — (nur intern) |

---

## Datenbankarchitektur

### Database-per-Service Pattern

Obwohl alle Dienste dieselbe PostgreSQL-Instanz nutzen, besitzt jeder Dienst eine separate Datenbank mit eigenem Datenbankbenutzer. Das verhindert unbeabsichtigte Datenüberschneidungen zwischen den Diensten:

```sql
-- docker/init.sql — wird beim ersten Start automatisch ausgeführt
CREATE USER java_user WITH PASSWORD 'java_password';
CREATE USER python_user WITH PASSWORD 'python_password';
CREATE DATABASE java_db OWNER java_user;
CREATE DATABASE python_db OWNER python_user;
```

### Java-Datenbankschema (`java_db`)

Das Java-Backend verwaltet alle Anwendungsdaten in der `java_db`. Spring Data JPA generiert das Schema automatisch aus den Entity-Klassen. Folgende Tabellen werden angelegt:

| Tabelle | Entity-Klasse | Beschreibung |
| :--- | :--- | :--- |
| `app_users` | `User.java` | Benutzerverwaltung mit Passwort-Hash und Rolle |
| `appointments` | `Appointment.java` | Terminbuchungen mit Status und Zeitfenster |
| `portfolio_items` | `PortfolioItem.java` | Portfolio-Einträge (Titel, Beschreibung, Tags) |
| `global_config` | `GlobalConfig.java` | Schlüssel-Wert-Paare für Laufzeitkonfiguration |

### Python-Datenbankschema (`python_db`)

Das Python-Backend führt ausschließlich Audit-Logs für KI-Anfragen. SQLModel (basierend auf SQLAlchemy) erstellt die Tabellen automatisch beim Start:

| Tabelle | Modell-Klasse | Beschreibung |
| :--- | :--- | :--- |
| `ai_audit_logs` | `AuditLog` | Vollständiges Protokoll aller KI-Anfragen |

---

## Frontend-Implementierung (Vue.js 3)

### Technologie-Stack

Das Frontend basiert auf Vue.js 3 mit der Composition API und Vite als Build-Werkzeug. Vue Router 4 ermöglicht clientseitiges Routing, Pinia verwaltet den globalen Anwendungsstatus. NGINX dient in der Produktionsumgebung als Web-Server und behandelt SPA-Routing durch eine `try_files`-Direktive.

**Abhängigkeiten:**
- `vue-frontend/package.json` — Vue 3, Vue Router 4, Pinia, Vite
- `vue-frontend/vite.config.js` — Build-Konfiguration mit `@`-Alias für `src/`
- `vue-frontend/nginx.conf` — NGINX mit SPA-Fallback

### Komponentenstruktur

```
vue-frontend/src/
├── main.js                  Anwendungseinstiegspunkt, Vue-App montieren
├── router/index.js          Routen-Definitionen mit Auth-Guard
├── stores/auth.js           Pinia Store für JWT-Token und Authentifizierungsstatus
├── services/api.js          Zentraler API-Client für Java- und Python-Backend
└── views/
    ├── Home.vue             Startseite mit SpaceX-inspiriertem Hero-Design
    ├── Profile.vue          Öffentliches Profil
    ├── TermineJava.vue      Terminbuchungssystem mit interaktivem Kalender
    ├── Portfolio.vue        Portfolio-Galerie
    ├── Login.vue            Login-Formular
    └── AdminDashboard.vue   Verwaltungsoberfläche (ROLE_ADMIN)
```

### State Management: Pinia Store (`stores/auth.js`)

Der Authentifizierungsstatus wird in einem Pinia Store verwaltet. Der JWT-Token wird im `localStorage` persistiert, damit die Sitzung bei einem Seitenneuladen erhalten bleibt. Zwei berechnete Eigenschaften (Getters) werten den Token-Payload aus:

- `isAuthenticated` — prüft, ob ein Token vorhanden ist
- `isAdmin` — dekodiert den JWT-Payload und vergleicht die Rolle mit `ROLE_ADMIN`

### Route Guards (`router/index.js`)

Ein `beforeEach`-Hook schützt alle Routen mit dem Meta-Flag `requiresAuth: true`. Nicht authentifizierte Nutzer werden automatisch zur Login-Seite umgeleitet.

### Zentraler API-Client (`services/api.js`)

Alle HTTP-Anfragen laufen über ein einziges API-Serviceobjekt. Das verhindert verteilte URLs im Code und stellt sicher, dass der `Authorization: Bearer <token>`-Header bei allen authentifizierten Anfragen automatisch gesetzt wird.

---

## Java Backend-Implementierung (Spring Boot 3 / Java 21 LTS)

### Framework und Konfiguration

Das Java-Backend setzt auf Spring Boot 3.2.0, Spring Security 6, Spring Data JPA und PostgreSQL. Der Quellcode wird mit **Java 21 LTS** kompiliert (`--release 21`), das Docker-Image basiert auf `eclipse-temurin:21-jdk`.

Folgende Maven-Abhängigkeiten sind im Einsatz:

| Abhängigkeit | Zweck |
| :--- | :--- |
| `spring-boot-starter-web` | REST-API mit eingebettetem Tomcat |
| `spring-boot-starter-security` | Spring Security 6 |
| `spring-boot-starter-data-jpa` | Spring Data JPA / Hibernate |
| `postgresql` (JDBC) | Datenbankverbindung |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` | JWT-Generierung und -Validierung |
| `lombok` | Boilerplate-Reduktion für Entitäten |

**Java-Version im Build:**
```xml
<!-- java-backend/pom.xml -->
<java.version>21</java.version>   <!-- Spring Boot Parent nutzt dies für --release 21 -->
```

### Paketstruktur

```
com.nspace/
├── JavaBackendApplication.java     @SpringBootApplication — Einstiegspunkt
├── config/
│   └── SecurityConfig.java         Spring Security: URL-Regeln, JWT-Filter, CORS
├── controller/
│   ├── AuthController.java         POST /api/auth/login, /register, /admin/register
│   ├── AppointmentController.java  GET/POST /api/termine, PUT /confirm, DELETE
│   ├── PortfolioController.java    GET /api/portfolio, POST/DELETE /api/admin/portfolio
│   └── AdminController.java        GET /api/admin/*, POST /api/admin/config
├── service/
│   ├── AuthService.java            Login, Registrierung, JWT-Ausgabe
│   ├── AppointmentService.java     Terminvalidierung und Geschäftslogik
│   ├── PortfolioService.java       Portfolio-CRUD
│   └── ConfigService.java          Laufzeit-Konfiguration mit Default-Werten
├── security/
│   ├── JwtUtil.java                JWT-Generierung (HMAC-SHA256, 10h Gültigkeit)
│   ├── JwtAuthenticationFilter.java OncePerRequestFilter: Token extrahieren & prüfen
│   └── CustomUserDetailsService.java UserDetails-Laden aus DB, Rollen-Mapping
├── model/
│   ├── User.java                   @Entity: app_users-Tabelle
│   ├── Appointment.java            @Entity: appointments-Tabelle
│   ├── AppointmentStatus.java      Enum: PENDING / CONFIRMED / CANCELLED
│   ├── PortfolioItem.java          @Entity: portfolio_items-Tabelle (Lombok)
│   └── GlobalConfig.java           @Entity: global_config key-value Tabelle
├── dto/
│   ├── LoginRequest.java           record: username, password
│   ├── LoginResponse.java          record: token, role
│   ├── RegisterRequest.java        record: username, password
│   ├── AppointmentRequest.java     record: startTime, customerName, note
│   └── AppointmentResponse.java    record: id, startTime, endTime, status, ...
└── repository/
    ├── UserRepository.java         JpaRepository + findByUsername
    ├── AppointmentRepository.java  JpaRepository + existsByStartTime
    ├── PortfolioRepository.java    JpaRepository
    └── ConfigRepository.java       JpaRepository + findByKey
```

### Schichten-Architektur

Das Java-Backend folgt konsequent dem Layered-Architecture-Muster:

1. **Controller** — Empfängt HTTP-Anfragen, validiert grob, delegiert an Service
2. **Service** — Enthält die gesamte Geschäftslogik und Validierungsregeln
3. **Repository** — Datenbankzugriff über Spring Data JPA (kein handgeschriebenes SQL)
4. **Model / Entity** — Abbildung der Datenbankstruktur auf Java-Klassen

### DTOs als Java Records

Alle Transferobjekte sind als **Java Records** implementiert (Java 16+, in Java 21 LTS vollständig stabilisiert). Records sind unveränderlich, benötigen keinen Boilerplate-Code und eignen sich ideal für Datencontainer:

```java
// Kompakte, unveränderliche DTO-Definition
public record AppointmentRequest(
    LocalDateTime startTime,
    String customerName,
    String note
) {}
```

### Geschäftslogik: Terminvalidierung (`AppointmentService.java`)

Der `AppointmentService` setzt folgende Buchungsregeln durch:

| Regel | Implementierung |
| :--- | :--- |
| Kein vergangener Termin | `startTime.isBefore(LocalDateTime.now())` |
| Buchungsfenster (dynamisch) | `configService.getInt("booking_window_months", 3)` |
| Nur Werktage | `DayOfWeek != SATURDAY && != SUNDAY` |
| Öffnungszeiten 10–15 Uhr | `LocalTime.of(10,0)` bis `LocalTime.of(15,0)` |
| Keine Doppeltbuchung | `repository.existsByStartTime(startTime)` |

Der Buchungszeitraum wird nicht hart kodiert, sondern aus der `global_config`-Tabelle gelesen. Admins können ihn zur Laufzeit über das Dashboard ändern.

### Sicherheitskonzept

**Authentifizierung:** Passwörter werden mit BCrypt gehasht gespeichert. Beim Login prüft der `AuthenticationManager` den Hash und gibt bei Erfolg einen JWT aus.

**JWT-Details:**
- Algorithmus: HMAC-SHA256
- Gültigkeitsdauer: 10 Stunden
- Nutzdaten: `subject` (Benutzername), `role`-Claim (z.B. `ROLE_ADMIN`)

**Autorisierung:** Spring Security wertet die Rolle im JWT bei jedem Request aus. Die `SecurityConfig` definiert die URL-basierten Zugriffsregeln:

```
GET  /api/auth/**           → öffentlich
GET  /api/portfolio         → öffentlich
GET  /api/termine           → öffentlich
GET  /api/termine/config    → öffentlich
PUT  /api/termine/*/confirm → ROLE_ADMIN
DELETE /api/termine/**      → ROLE_ADMIN
/api/admin/**               → ROLE_ADMIN
*                           → authentifiziert
```

### Unit-Tests (`AppointmentServiceTest.java`)

Das Projekt enthält JUnit 5 / Mockito-Tests für den Service-Layer:
- `getAllAppointments_ShouldReturnList` — prüft korrekte Listenrückgabe
- `createAppointment_ShouldSaveAndReturnResponse` — prüft das Speichern und die Response

Ausführung: `mvn clean test` → BUILD SUCCESS | 2/2 Tests bestanden | kompiliert mit `release 21`

---

## Python Backend-Implementierung (FastAPI)

### Framework und Abhängigkeiten

Das Python-Backend setzt auf FastAPI mit Uvicorn als ASGI-Server. SQLModel (SQLAlchemy + Pydantic) verwaltet die Datenbankanbindung. Das Docker-Image basiert auf `python:3.11-slim`.

### Paketstruktur

```
python-backend/src/
├── main.py                      FastAPI-App, CORS-Middleware, Router-Registrierung
├── config.py                    Einstellungen über Pydantic BaseSettings / .env
├── database.py                  SQLModel Engine und Session-Factory
├── prompts.py                   System-Prompt-Templates für KI-Anfragen
├── utils.py                     Hilfsfunktionen
├── models/
│   ├── dtos.py                  Pydantic-Modelle: InferRequest, InferResponse, ...
│   └── audit.py                 SQLModel-Tabelle ai_audit_logs
└── services/
    ├── ai_provider.py           Abstrakte AIProvider-Klasse + alle Implementierungen
    ├── provider_factory.py      Factory mit Singleton-Cache und MockProvider-Fallback
    ├── ai_service.py            Orchestrierung: Provider aufrufen, Latenz messen, loggen
    └── audit_service.py         Datenbankzugriff für Audit-Logging
└── routers/
    └── ai_router.py             FastAPI-Router: GET /info, POST /infer, POST /generate-ideas
```

### Multi-Provider-Architektur

Das Kernstück des Python-Backends ist das **Factory Pattern** mit **Polymorphismus**:

```python
# services/ai_provider.py — Gemeinsamer Vertrag für alle Provider
class AIProvider(ABC):
    @abstractmethod
    def generate(self, prompt: str) -> str:
        pass
```

Alle vier Provider implementieren diese Schnittstelle:

| Klasse | Provider-Name | Basis |
| :--- | :--- | :--- |
| `OpenAIProvider` | `openai` | Direkt OpenAI API |
| `DeepSeekProvider` | `deepseek` | Erbt von `OpenAICompatibleProvider` |
| `OllamaProvider` | `ollama` | Lokaler Ollama-Server |
| `MockProvider` | `mock` | Feste Testantworten, kein API-Key nötig |

Die `ProviderFactory` cached Instanzen und fällt bei fehlendem API-Key auf den `MockProvider` zurück.

### Audit Logging

Jede KI-Anfrage wird in `ai_audit_logs` protokolliert:
- Provider, Prompt-Vorschau, Antwort-Vorschau, Latenz in ms, Zeitstempel

Die Latenz wird über `perf_counter()` gemessen — direkt im `AIService` vor und nach dem Provider-Aufruf. Das ermöglicht präzise Provider-Vergleiche.

### API-Endpunkte

| Methode | Pfad | Beschreibung |
| :--- | :--- | :--- |
| `GET` | `/api/ki/info` | Aktiver Provider, verfügbare Provider |
| `POST` | `/api/ki/infer` | Freie Textgenerierung mit wählbarem Provider |
| `POST` | `/api/ki/generate-ideas` | Strukturierte Ideen-Generierung |

---

## Docker-Build-Strategie

### Multi-Stage Build (Frontend)

Das Vue.js-Frontend nutzt einen zweistufigen Docker-Build:
1. **Build-Stage** (`node:20-slim`): `npm run build` — erzeugt den `dist/`-Ordner
2. **Runtime-Stage** (`nginx:alpine`): NGINX liefert die statischen Assets aus

Das finale Image enthält kein Node.js und keine Build-Werkzeuge — nur NGINX und die kompilierten Dateien.

### Java Backend

Das Java-Backend nutzt `eclipse-temurin:21-jdk` als Basis. Maven wird im Container installiert, das Projekt gebaut und Maven anschließend wieder entfernt, um die Image-Größe zu minimieren:

```dockerfile
FROM eclipse-temurin:21-jdk
RUN apt-get update && apt-get install -y maven && \
    mvn -B clean package && \
    apt-get remove -y maven && apt-get autoremove -y
CMD ["sh", "-c", "java -jar target/*.jar"]
```

### Python Backend

Das Python-Backend basiert auf `python:3.11-slim`. Die `requirements.txt` wird separat von der Quelldatei kopiert, damit Docker den Abhängigkeits-Layer cachen kann und nicht bei jeder Code-Änderung neu installiert.

---

## Sicherheitsarchitektur (Zusammenfassung)

| Mechanismus | Implementierung |
| :--- | :--- |
| Passwort-Hashing | BCrypt (Spring Security DefaultPasswordEncoder) |
| Token-Format | JWT (HMAC-SHA256, 10h Gültigkeit) |
| Sitzungsstrategie | Stateless — kein Server-State, kein Session-Store |
| Rollen | `ROLE_USER`, `ROLE_ADMIN` — im JWT-Payload kodiert |
| Zugriffskontrolle | URL-basierte Regeln in `SecurityConfig`, HTTP-Methoden-granular |
| CORS | Explizit erlaubte Origins in `SecurityConfig` und FastAPI `CORSMiddleware` |
| CSRF | Deaktiviert — nicht benötigt bei Bearer-Token-Auth ohne Cookies |
| Netzwerk | Backend-Services ohne externe Ports — nur internes Docker-Netzwerk |

---

## Entwicklung & Tests

### Lokale Entwicklung ohne Docker

Alle drei Dienste können unabhängig ohne Docker gestartet werden:

```bash
# Java Backend
cd java-backend && mvn spring-boot:run

# Python Backend
cd python-backend && uvicorn src.main:app --reload --port 8000

# Vue Frontend
cd vue-frontend && npm run dev
```

### Mit Docker Compose

```bash
docker compose up --build
```

Alle Dienste starten geordnet (Postgres → Backends → Frontend).

### Tests

```bash
cd java-backend && mvn clean test
# Ergebnis: BUILD SUCCESS | Tests run: 2 | Failures: 0 | Errors: 0
# Compiler: debug release 21
```

---

## Bekannte Eigenheiten und gelöste Probleme

| Problem | Ursache | Lösung |
| :--- | :--- | :--- |
| Spring Boot kompilierte mit `release 17` statt `release 21` | Spring Boot Parent POM nutzt `${java.version}` für `--release`, nicht `maven.compiler.source` | `<java.version>21</java.version>` in `pom.xml` |
| Java 21 Warnungen im Test (Mockito/ByteBuddy) | Dynamisches Agent-Loading ohne explizite Erlaubnis | `maven-surefire-plugin` mit `-XX:+EnableDynamicAgentLoading` |
| Admin konnte sich einloggen, aber keine Admin-Aktionen ausführen | JWT enthielt Rolle, aber `CustomUserDetailsService` mappte sie nicht als Spring Authority | `new SimpleGrantedAuthority(user.getRole())` |

---

## Fazit

Das NSPACE Portfolio Projekt demonstriert eine vollständige, produktionsnahe Webanwendungsarchitektur. Vom containerisierten Datenbankschema über typsichere Java Records und stateless JWT-Authentifizierung bis hin zum austauschbaren KI-Provider-System mit Audit-Logging — alle Schichten des Systems sind bewusst und nach anerkannten Software-Engineering-Prinzipien gestaltet.

Die Kombination aus Java 21 LTS und Spring Boot 3 auf der einen Seite sowie Python FastAPI mit dem Factory-Pattern auf der anderen ist ein Paradebeispiel für den Best-of-Breed-Ansatz: jede Sprache und jedes Framework dort eingesetzt, wo ihre Stärken liegen.
