BACK – Java Backend
====================

Dieses Dokument ist mein persönliches Protokoll zum Java-Backend dieses Projekts. Ich halte hier fest, was implementiert ist, wie die Paketstruktur aufgebaut ist und warum bestimmte Entscheidungen so getroffen wurden – verständlich genug, um nach einer längeren Pause direkt wieder einsteigen zu können.

---

Aktueller Stand
---------------

Die Anwendung ist eine vollständige Spring Boot 3.2.0 Anwendung, die auf Java 21 LTS läuft. Sie verwaltet Terminbuchungen, ein Benutzersystem mit JWT-Authentifizierung, Portfolio-Einträge und dynamische Systemkonfiguration – alles gesichert über Spring Security mit rollenbasierter Zugriffskontrolle.

---

Paketstruktur
-------------

```
src/main/java/com/nspace/
├── config/
│   └── SecurityConfig.java         – CORS, JWT-Filter, HTTP-Sicherheitsregeln
├── controller/
│   ├── AuthController.java         – Login und Registrierung (/api/auth)
│   ├── AppointmentController.java  – Terminverwaltung (/api/termine)
│   ├── AdminController.java        – Admin-Verwaltung (/api/admin)
│   └── PortfolioController.java    – Portfolio (/api/portfolio)
├── dto/
│   ├── LoginRequest.java           – Eingabe für Login (Java Record)
│   ├── LoginResponse.java          – Ausgabe nach Login: JWT-Token
│   ├── RegisterRequest.java        – Eingabe für Registrierung
│   ├── AppointmentRequest.java     – Eingabe für neue Terminbuchung
│   └── AppointmentResponse.java    – Ausgabe für Terminantworten
├── model/
│   ├── User.java                   – Benutzerentität (Tabelle: app_users)
│   ├── Appointment.java            – Terminentität (Tabelle: appointments)
│   ├── AppointmentStatus.java      – Status-Enum: PENDING, CONFIRMED, CANCELLED
│   ├── PortfolioItem.java          – Portfolio-Einträge (Tabelle: portfolio_items)
│   └── GlobalConfig.java           – Schlüssel-Wert-Konfiguration (Tabelle: global_config)
├── repository/
│   ├── UserRepository.java         – JPA Repository für Benutzer
│   ├── AppointmentRepository.java  – JPA Repository für Termine (inkl. Slot-Check)
│   ├── PortfolioRepository.java    – JPA Repository für Portfolio
│   └── ConfigRepository.java       – JPA Repository für Konfiguration
├── security/
│   ├── JwtUtil.java                – JWT erzeugen, parsen, validieren
│   ├── JwtAuthenticationFilter.java – Filter: prüft jede Anfrage auf JWT
│   └── CustomUserDetailsService.java – Lädt Benutzer aus DB für Spring Security
└── service/
    ├── AuthService.java            – Login, Registrierung, Token-Ausgabe
    ├── AppointmentService.java     – Geschäftslogik für Termine und Validierung
    ├── PortfolioService.java       – CRUD für Portfolio-Einträge
    └── ConfigService.java          – Lesen/Schreiben der globalen Konfiguration
```

---

Wichtigste Dateien
------------------

**pom.xml**
Maven-Projektdatei. Java-Version ist über `<java.version>21</java.version>` konfiguriert, was dafür sorgt, dass der Spring Boot Parent die richtige `--release 21`-Flag an den Compiler weitergibt. Spring Boot 3.2.0 ist das Eltern-Projekt. Das Surefire-Plugin ist mit `-XX:+EnableDynamicAgentLoading` konfiguriert, um Java-21-Warnungen bei Tests zu unterdrücken.

**Dockerfile**
Mehrstufiger Build: Im ersten Stage wird die Anwendung mit Maven kompiliert und ein JAR erzeugt. Im zweiten Stage läuft das JAR auf einem schlanken eclipse-temurin:21-jdk Image ohne Build-Werkzeuge.

**application.properties**
Spring Boot Konfiguration: Port 8080 (intern), aktives Profil `docker`, Datenbankverbindung über Umgebungsvariablen (`${DB_HOST}`, `${DB_USER}` usw.). JWT-Secret kommt ebenfalls aus der Umgebung.

---

Wie Spring Boot hier funktioniert
----------------------------------

Die Annotation `@SpringBootApplication` auf der Hauptklasse löst automatisch die Component Scan, Auto-Configuration und die eingebettete Tomcat-Instanz aus. Alle mit `@Service`, `@Repository`, `@RestController` oder `@Component` markierten Klassen werden von Spring als sogenannte Beans verwaltet – das bedeutet, Spring kümmert sich um Erstellung und Abhängigkeitsinjektion (Dependency Injection).

Der Ablauf einer Anfrage von außen:
1. NGINX (Frontend-Container) leitet die Anfrage weiter an den Java-Container auf Port 8080
2. Spring Security prüft in der `SecurityFilterChain`, ob der Endpunkt geschützt ist
3. Wenn ja: `JwtAuthenticationFilter` extrahiert und prüft das Bearer-Token
4. Nach erfolgreicher Authentifizierung landet die Anfrage im zuständigen Controller
5. Der Controller delegiert an den Service, der die Geschäftslogik enthält
6. Der Service ruft über das Repository die Datenbank ab
7. Das Ergebnis wird als JSON-Antwort zurückgegeben

---

Sicherheitskonzept im Detail
------------------------------

Die `SecurityConfig` definiert die Zugriffsregeln:

- Alle `/api/auth/**`-Pfade sind öffentlich (Login und Registrierung)
- Terminlesen und -buchen sind öffentlich (kein Login nötig)
- Bestätigen und Löschen von Terminen: nur `ROLE_ADMIN`
- `/api/admin/**`: nur `ROLE_ADMIN`
- `/api/portfolio` GET: öffentlich; POST/DELETE nur Admin
- Alles andere: authentifiziert

Das Session-Management ist stateless – es gibt keine serverseitige Session. Stattdessen enthält jede Anfrage das JWT im Authorization-Header, das beim Eingang geprüft wird.

BCrypt hasht die Passwörter mit adaptivem Kostenfaktor, was Brute-Force-Angriffe deutlich verlangsamt.

---

Terminbuchung: Geschäftsregeln
--------------------------------

Der `AppointmentService` enthält die vollständige Validierungslogik. Buchungen werden abgelehnt, wenn:

- Der Zeitpunkt in der Vergangenheit liegt
- Das Buchungsfenster überschritten wird (dynamisch aus `global_config` geladen, Default: 3 Monate)
- Der Tag ein Samstag oder Sonntag ist
- Die Uhrzeit außerhalb von 10:00–15:00 liegt
- Der gewählte Slot bereits belegt ist (Prüfung via `existsByStartTime`)

Diese serverseitige Validierung ist unabhängig vom Frontend und schützt vor manipulierten Anfragen.

---

Lokale Entwicklung
------------------

Build und Tests lokal ausführen (Maven 3.9 + JDK 21 vorausgesetzt):

```bash
cd java-backend

# Vollständig bauen und testen
mvn clean verify

# Nur starten (PostgreSQL muss laufen)
mvn spring-boot:run

# Docker-Compose nur für Datenbank starten
docker compose up postgres -d
```

Tests ausführen:

```bash
mvn test
# Ergebnis: 2/2 Tests bestanden (AppointmentServiceTest)
```

Docker Image lokal bauen:

```bash
docker build -t nspace-java-backend:local ./java-backend
docker run --rm -p 8080:8080 --env-file .env nspace-java-backend:local
```

---

Troubleshooting
---------------

**Build schlägt mit Java-Versionsfehler fehl:**
Sicherstellen, dass `JAVA_HOME` auf JDK 21 zeigt. Prüfen mit:
```bash
java -version
mvn -v
```

**Spring Boot startet nicht (DB-Verbindung):**
Alle Umgebungsvariablen (`DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`) müssen gesetzt sein. Im Docker-Kontext geschieht das automatisch über `docker-compose.yml`.

**JWT-Fehler (401 Unauthorized):**
Token-Ablaufzeit prüfen (10 Stunden). Das `JWT_SECRET` muss mindestens 256 Bit lang sein, sonst schlägt die HMAC-Signierung fehl.

**Compiler gibt `release 17` statt `release 21` aus:**
Sicherstellen, dass `<java.version>21</java.version>` in `pom.xml` gesetzt ist. Spring Boot Parent nutzt diese Property für das `--release`-Flag des Maven Compiler Plugins. `maven.compiler.source/target` allein reichen nicht.

---

Offene Punkte
-------------

- E-Mail-Bestätigung bei Terminbuchung (ausstehend, Sprint 11)
- HttpOnly Cookies für JWT statt localStorage (Sicherheits-Hardening, Sprint 11)
- Admin-Route Guards im Frontend für Nicht-Admin-Benutzer (Sprint 11)
