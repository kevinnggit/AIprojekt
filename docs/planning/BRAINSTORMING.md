# Brainstorming & Ideen

Stand: März 2026
---

## Was bereits steht

Die Grundstruktur läuft: Terminbuchung, Admin-Dashboard, Portfolio-CMS, KI mit mehreren Anbietern (OpenAI, DeepSeek, Ollama, Mock), JWT-Auth mit RBAC. Das reicht für einen technischen Prototyp, aber noch nicht für ein echtes Produkt.

---

## Sicherheit — dringend

**JWT aus dem LocalStorage raus.**
Das ist der größte offene Schwachpunkt. Ein JavaScript-Angriff (XSS) kann den Token aus dem LocalStorage lesen. Die Lösung ist bereits geplant (SEC-001): den Token als HttpOnly Cookie setzen. Browser schicken den Cookie dann automatisch mit — kein JS-Zugriff möglich. Dazu braucht man:
- Einen `/api/auth/me`-Endpunkt, der beim App-Start prüft ob das Cookie noch gültig ist
- Logout-Endpunkt, der das Cookie auf Max-Age=0 setzt
- Im Pinia-Store: `isAdmin`-Check über die Serverantwort statt JWT-Decode im Browser

**Rate Limiting auf Login-Endpunkt.**
Momentan kann jemand unbegrenzt Passwörter ausprobieren. Ein einfaches Rate-Limit (z.B. 5 Versuche pro Minute per IP) über Spring Security oder einen Nginx-Layer wäre sinnvoll.

**HTTPS.**
Solange alles auf localhost läuft, egal. Für Produktivbetrieb ist Nginx + Let's Encrypt der Standardweg. Im docker-compose.yml einen Nginx-Container dazuschalten, der als Reverse-Proxy fungiert. Dann läuft HTTPS außerhalb, die Backends bleiben intern unverändert.

---

## Terminbuchung — was noch fehlt

**Bestätigungsmail.**
Das ist aktuell der größte funktionale Riss: Man bucht einen Termin und hört danach nichts mehr. Das wirkt kaputt. Spring Boot hat `spring-boot-starter-mail`, MailHog läuft als Docker-Container für die Entwicklung. Inhalt der Mail: Datum, Uhrzeit, Name — mehr braucht es anfangs nicht. Optional: 24-Stunden-Erinnerung per Scheduler (`@Scheduled`).

**iCal / Kalender-Export.**
Ein `.ics`-Download nach der Buchung. Dann landet der Termin direkt im Kalender des Kunden (Google Calendar, Outlook). Technisch: Java `ical4j` Library, Response mit `Content-Type: text/calendar`. Kleiner Aufwand, großer Komfortgewinn.

**Stornierung durch den Kunden.**
Aktuell kann nur der Admin löschen. Sinnvoll wäre ein Link in der Bestätigungsmail mit einem signierten Token — klickt der Kunde drauf, wird der Termin storniert. Kein Login nötig.

**Puffer zwischen Terminen.**
Aktuell sind stündliche Slots ohne Pause. Wenn eine Beratung mal länger dauert, überschneidet sich das. Konfigurierbar über `global_config`: z.B. `slot_duration_minutes = 60`, `buffer_minutes = 15`.

---

## KI-Bereich — Potenzial liegt brach

**Modellauswahl je Anbieter.**
Die Provider-Auswahl ist da (OpenAI / DeepSeek / Ollama / Mock), aber die Modellauswahl fehlt noch. OpenAI hat gpt-4o und gpt-3.5-turbo — unterschiedliche Kosten, unterschiedliche Qualität. Das Dropdown wäre sinnvoll.

**Gemini und Claude als Provider.**
DeepSeek und Ollama sind schon drin. Google Gemini und Anthropic Claude wären die logischen nächsten Ergänzungen — beide haben OpenAI-kompatible APIs, der Aufwand ist minimal (neue Subklasse von `OpenAICompatibleProvider`, andere `base_url`).

**Audit-Log im Admin sichtbar machen.**
Die Daten stecken bereits in der Datenbank (`ai_audit_logs`): welcher Provider, wie lang, was wurde gefragt. Das ist wertvoll. Im Admin-Dashboard als Tabelle oder einfaches Diagramm (Latenz pro Provider, Anfragen pro Tag) anzeigen.

**System-Prompt konfigurierbar machen.**
Aktuell ist der System-Prompt hardcodiert in `prompts.py`. Wenn man ihn über das Admin-Interface ändern kann (in `global_config` speichern), kann man das Verhalten der KI anpassen ohne Deployment.

**KI-Vergleichsseite.**
Das stand schon in der ursprünglichen Idee (Punkt 7): "Vergleich neuer KIs". Eine Seite, auf der man denselben Prompt an verschiedene Provider schickt und die Antworten (und Latenz) nebeneinander sieht. Das ist sowohl für Besucher interessant als auch ein konkreter Showcase der eigenen Architektur.

---

## Admin-Dashboard — Erweiterungen

**Live-Statistiken.**
Statt nur einer Terminliste: oben ein Überblick-Banner mit "X offene Termine heute", "Y Bestätigungen ausstehend", "Z KI-Anfragen diese Woche". Technisch ein `/api/admin/stats`-Endpunkt der diese Zahlen zurückgibt.

**Benutzerübersicht.**
Aktuell kann der Admin Admins anlegen, aber eine Übersicht aller registrierten Benutzer fehlt komplett. Ein GET `/api/admin/users` wäre ausreichend für den Anfang.

**Aktivitätslog.**
Wer hat sich wann eingeloggt, welche Termine wurden gebucht oder bestätigt. Nicht für die Produktion kritisch, aber für eigene Übersicht nützlich. Kann in der `global_config`-Idee aufbauend auf einer neuen `activity_log`-Tabelle umgesetzt werden.

---

## Frontend / UX

**Sticky Header.**
Steht schon in den Notizen. Der Header soll beim Scrollen sichtbar bleiben. CSS `position: sticky; top: 0;` + ein bisschen `backdrop-filter: blur()` damit er nicht blind wirkt.

**Ladeanimationen.**
Wenn ein API-Call läuft (Termin buchen, KI-Anfrage), gibt es keinen visuellen Hinweis. Ein einfacher Spinner oder Skeleton-State würde das deutlich professioneller wirken lassen.

**Toast-Benachrichtigungen.**
Momentan: Fehlermeldungen landen irgendwie im Interface. Besser: Ein zentrales Toast-System (oben rechts erscheint kurz "Termin gebucht ✓" oder "Fehler: Slot bereits belegt"). In Vue gibt es dafür kleine Libraries (vue-toastification) oder man baut es selbst.

**Mobile-Ansicht prüfen.**
Die App wurde auf Desktop entwickelt. Wie sieht der Kalender auf einem iPhone aus? Die Terminbuchung auf einem kleinen Bildschirm? Das sollte man einmal durchgehen und die kritischen Views responsive machen.

**Dark/Light Mode.**
Das SpaceX-Design ist dark-first. Ein Toggle wäre nice-to-have, allerdings geringer Mehrwert für den Aufwand — eher als spätere Ergänzung sinnvoll.

**Bild im profil**

---

## Noch fehlende Seiten 

**Seite 6 — Ausstellung (IT-Themenwelt).**
Projektmanagement, RPA, KI, IT-Security als Mini-Artikel oder Karten. Das könnte sogar über das Portfolio-CMS befüllt werden — wenn man Tags wie `kategorie: rpa` einführt und nach Kategorie filtert. Dann braucht es keine extra Seite, sondern nur ein erweitertes Portfolio-Feature.

**Homepage — persönliche Story.**
Aktuell gibt es Buttons und Design, aber keine klare Aussage: "Ich bin [Name], ich mache [X], und du kannst bei mir [Y]." Das fehlt auf der ersten Seite. Ein kurzer Hero-Text unterhalb des Logos würde den Unterschied machen.

---

## Monetarisierung (wenn relevant)

Stripe-Integration vor der Terminbestätigung: Erst zahlen, dann ist der Termin verbindlich. Das wäre der sauberste Flow. Technisch: Stripe Checkout Session nach der Buchung, Webhook bei erfolgreicher Zahlung setzt Status auf `CONFIRMED`. Aufwand: mittel bis hoch, Nutzen: hoch wenn das Ziel ein echter Buchungsservice ist.

---

## Infrastruktur

**Prometheus + Grafana** für Metriken (Spring Boot Actuator hat das out-of-the-box). Für professionelles Monitoring, aber für ein Portfolio-Projekt wahrscheinlich überdimensioniert — eher dokumentieren als bauen.

**Datenbankbackups.** Ein täglicher `pg_dump` als Cron-Job im Docker. Nicht kritisch für Entwicklung, aber wenn echte Kundentermine drin stehen, unverzichtbar.

---

## Prioritäten (persönliche Einschätzung)

Was am meisten bringt ohne zu viel Aufwand:

1. Bestätigungsmail — größte UX-Lücke, mittlerer Aufwand
2. HttpOnly Cookie — sicherheitsrelevant, Aufwand überschaubar
3. Sticky Header + Toasts — sichtbarer Impact, geringer Aufwand
4. Modellauswahl bei der KI — komplettiert ein halbfertiges Feature
5. Admin-Statistiken — macht das Dashboard erst richtig nützlich
