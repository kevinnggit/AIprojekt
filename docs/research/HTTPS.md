# Research: HTTPS & Security Strategy

**Status:** Draft
**Date:** 11.01.2026

## Problemstellung
Aktuell läuft die App über `http://localhost`. Daten (Passwörter, Emails) werden im Klartext übertragen.
Für einen Produktivbetrieb (und für seriöses Auftreten) ist HTTPS (`https://`) zwingend erforderlich.

## Analyse der Optionen

### Option A: Self-Signed Certificates (Local Dev)
Wir erstellen ein Fake-Zertifikat.
*   **Vorteil:** Schnell, kein Domain-Name nötig.
*   **Nachteil:** Browser zeigt riesige Warnung "NICHT SICHER".
*   **Verdict:** Gut zum Testen der Technik, schlecht für User Experience.

### Option B: Reverse Proxy mit Let's Encrypt (Production)
Wir schalten einen Webserver (Nginx oder Traefik) *vor* unsere Container.
User -> Nginx (Port 443/HTTPS) -> Vue/Java/Python (Internal Network).
*   **Vorteil:** Industriestandard. Automatische Zertifikate.
*   **Nachteil:** Benötigt eine echte Domain (z.B. `mein-portfolio.de`) und einen öffentlichen Server (VPS). Funktioniert nicht einfach so auf "localhost" ohne Tricks.

### Option C: Cloudflare Tunnel (Easy Mode for Home Hosting)
Wenn du das Projekt zuhause hostest (auf deinem PC) und öffentlich machen willst.
*   **Vorteil:** Kein Port-Forwarding, Gratis SSL.

## Empfehlung für JETZT (Localhost Entwicklung)

Da wir noch entwickeln: **Ignoriere HTTPS vorerst.**
Der Aufwand, HTTPS auf `localhost` sauber laufen zu lassen (ohne Warnungen), ist hoch und bringt für die *Logik* der App keinen Mehrwert.

**Wenn** wir Security simulieren wollen, implementieren wir zuerst die **Applikations-Sicherheit** (siehe Task SEC-001):
1.  **Route Guards:** Verhindern Zugriff auf `/admin` ohne Login.
2.  **HttpOnly Cookies:** Verhindern, dass Javascript Session-Token klaut (Best Practice).

## Roadmap Step-by-Step
1.  **Sprint 8:** App-Level Security (Login-Zwischenprüfung).
2.  **Pre-Release:** Docker-Compose erweitern um Nginx-Container.
3.  **Release:** Domain kaufen, DNS setzen, Let's Encrypt aktivieren.
