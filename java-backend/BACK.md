BACK - Java Backend
====================

Dieses Dokument ist mein persönliches Protokoll zum Java-Backend dieses Projekts. Ich schreibe hier fest, was aktuell implementiert ist, wie die Ordnerstruktur aussieht und wie das Spring Boot Teil grundsätzlich funktioniert — so, wie ich es beim Lernen verstehen würde.

Kurzfassung des aktuellen Stands
--------------------------------
- Das Backend ist eine kleine Spring Boot Anwendung unter `src/main/java/com/nspace/JavaBackendApplication.java`.
- Es gibt ein paar einfache Endpunkte (Status / Health / Termine) — momentan nur Platzhalter-Antworten ohne Datenbank.
- Ich habe das Projekt so angepasst, dass es auf Java 21 zielt (Maven-Compiler-Properties auf 21 gesetzt) und das Dockerfile auf `openjdk:21-jdk-slim` umgestellt ist. Dadurch läuft das Image auf der aktuellen LTS-Java-Version.

Wichtigste Dateien
------------------
- `pom.xml`  
  Maven-Projektdatei: Abhängigkeiten, Spring Boot Version und Compiler-Target (jetzt Java 21).

- `Dockerfile`  
  Baut das Image mit OpenJDK, installiert kurz Maven, erstellt das JAR und entfernt Build-Tools wieder — das Resultat ist ein schlankes Laufzeit-Image.

- `src/main/java/com/nspace/JavaBackendApplication.java`  
  Die Hauptklasse der Spring-Boot-App. Sie startet die Anwendung und enthält einfache REST-Handler.

- `src/main/resources/application.properties`  
  Konfigurationsdatei für Spring Boot (Port, Profile, ggf. CORS etc.).

Wie das Spring Boot Teil funktioniert (einfach erklärt)
------------------------------------------------------
Ich fasse die Grundlagen so zusammen, dass ich sie beim Lernen immer wieder nachlesen kann:

- Spring Boot ist ein Framework, das eine Java-Anwendung mit möglichst wenig Boilerplate startet. Die Annotation `@SpringBootApplication` sorgt dafür, dass Spring automatisch konfiguriert und die eingebettete Server-Engine (standardmäßig Tomcat) gestartet wird.

- Die Methode `public static void main(String[] args)` ruft `SpringApplication.run(...)` auf. Das ist der Einstiegspunkt der App.

- In dieser Anwendung ist die gleiche Klasse auch `@RestController`. Das heißt: Methoden mit `@GetMapping`, `@PostMapping` usw. werden zu HTTP-Endpunkten. Beispiel:
  - `@GetMapping("/")` liefert einen kurzen Text, um zu prüfen, ob der Service läuft.
  - `@GetMapping("/health")` liefert einen einfachen Health-Check.
  - `@GetMapping("/api/termine")` ist ein Platzhalter für Termine-APIs.

- Wenn ich später Logik oder eine Datenbank brauche, würde ich:
  1. DTOs/Entity-Klassen in `src/main/java/com/nspace/domain` bzw. `model` anlegen,
  2. Repositories (Spring Data JPA) unter `repository` erstellen,
  3. Service-Klassen unter `service` für Geschäftslogik anlegen,
  4. Controller unter `controller` halten, die Services aufrufen.

Warum ich auf Java 21 umgestellt habe
------------------------------------
Ich habe die Compiler-Settings und das Docker-Base-Image auf Java 21 angepasst, weil 21 die aktuellste LTS-Version ist. Das gibt mir Zugriff auf neue Sprach-Features und sorgt dafür, dass die App länger wartbar bleibt.

Wie ich lokal entwickle und prüfe
--------------------------------
Kurzbefehle, die ich nutze:

- Lokaler Build (Schnell-Check, Tests überspringen):

```bash
cd java-backend
mvn clean package -DskipTests
```

- App lokal starten (nach Build):

```bash
java -jar target/*.jar
# oder während Entwicklung
mvn spring-boot:run
```

- Docker Image bauen und starten:

```bash
# aus Projekt-Root
docker build -t ai-projekt-java-backend:java21 ./java-backend
docker run --rm -p 8080:8080 ai-projekt-java-backend:java21
```

Tipps / Troubleshooting
-----------------------
- Wenn der Build wegen Java-Versionen scheitert, prüfe mit `java -version` und `mvn -v`, welche JDK/Maven-Versionen aktiv sind. Ich stelle sicher, dass JAVA_HOME auf ein JDK 21 zeigt.
- Wenn Docker-Image Fehler beim Bauen zeigt, lese die Logs beim `mvn package` Schritt; oft liegt es an fehlenden Abhängigkeiten oder Netzwerk/Proxy-Problemen während `apt-get`.
- Für späteren Einsatz will ich die Maven-Toolchain oder `maven-compiler-plugin` mit `<release>21</release>` ergänzen, damit die Builds reproduzierbar sind.

Nächste Schritte (meine TODOs)
-----------------------------
- Echte Termine-API implementieren (mit DTOs, Service und Repository).
- Unit-Tests für Controller und Services schreiben.
- Optional: Spring Security hinzufügen (für Auth).
- CI: Pipeline anpassen, damit Builds mit JDK 21 laufen.

Kleine Anmerkung zu diesem Dokument
----------------------------------
Ich habe hier bewusst einfache, kurze Sätze gewählt, damit ich das beim Lernen gut verstehe. Falls ich später tiefer einsteige, erweitere ich dieses Protokoll mit Class-Diagrammen und konkreten API-Beispielen.

---
Wenn du willst, passe ich dieses BACK.md noch an (z. B. ausführlichere Beispiele, Postman-Sammlung oder Swagger/OpenAPI-Snippets).