# Recherche: HTTPS und Sicherheitsstrategie

Stand: 11.01.2026

## Die Ausgangslage

Die App läuft aktuell über `http://localhost`. Passwörter und sonstige Daten werden unverschlüsselt übertragen. Für produktiven Betrieb — und für ein seriöses Auftreten — ist HTTPS zwingend.

## Die drei Wege zu HTTPS

### Selbst-signierte Zertifikate (lokale Entwicklung)
Ein gefälschtes Zertifikat, das lokal erstellt wird.
- **Vorteil:** Schnell eingerichtet, kein Domain-Name nötig.
- **Nachteil:** Browser zeigt eine fette Warnung "NICHT SICHER".
- **Fazit:** Gut zum Testen der Technik, schlecht für echte Nutzer.

### Reverse Proxy mit Let's Encrypt (Produktion)
Ein Webserver (Nginx oder Traefik) sitzt vor den Containern und übernimmt die TLS-Terminierung.
`Nutzer → Nginx (Port 443/HTTPS) → Vue/Java/Python (internes Netzwerk)`
- **Vorteil:** Industriestandard, automatische Zertifikats-Erneuerung.
- **Nachteil:** Braucht eine echte Domain (z.B. `mein-portfolio.de`) und einen öffentlichen Server (VPS). Funktioniert auf `localhost` nicht ohne Tricks.

### Cloudflare Tunnel (für Heimhosting)
Wenn das Projekt auf dem eigenen PC gehostet und öffentlich erreichbar sein soll.
- **Vorteil:** Kein Port-Forwarding nötig, kostenloses SSL.

## Was wir jetzt machen

Da wir noch entwickeln: **HTTPS vorerst ignorieren.**

Der Aufwand, HTTPS auf `localhost` sauber zum Laufen zu bringen (ohne Browser-Warnungen), ist hoch und bringt für die eigentliche App-Logik nichts.

Wenn wir Security simulieren wollen, fangen wir mit der Applikations-Sicherheit an (SEC-001):
1. **Route Guards:** Kein Zugriff auf `/admin` ohne Login.
2. **HttpOnly Cookies:** JavaScript kann den Session-Token nicht mehr stehlen.

## Der Weg nach vorne

1. **Sprint 11:** App-Level Security (Login-Absicherung).
2. **Vor Release:** `docker-compose.yml` um Nginx-Container erweitern.
3. **Release:** Domain kaufen, DNS setzen, Let's Encrypt aktivieren.
