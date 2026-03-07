# KI-Interface: Provider- und Modellauswahl

**Feature:** AI-002
**Sprint:** 08

## Was sich ändern soll

Die aktuelle KI-Seite zeigt immer alle Felder gleichzeitig. Das ist unübersichtlich. Wir wollen, dass der Nutzer erst den Provider wählt, dann das Modell, und dann den Modus (Chat oder Ideengenerator). Was nicht gebraucht wird, ist ausgeblendet.

## Provider und Modelle

Das Auswahlmenü ist hierarchisch:
- **Provider:** OpenAI, DeepSeek, Mock (weitere geplant)
- **Modell:** Ändert sich je nach Provider
    - OpenAI: `gpt-4o`, `gpt-3.5-turbo`
    - DeepSeek: `v3`

## Aufgaben-Modus

Dropdown oder Tabs für den "Modus":
1. **Chat / Analyse** (Standard) — Textfeld für freien Input.
2. **Projekt-Ideen** — Formular für Thema und Anzahl der Ideen.
3. **Bildgenerierung** (Platzhalter) — Prompt-Feld für spätere Implementierung.

Wenn "Chat" gewählt ist, ist das Ideen-Formular nicht sichtbar — und umgekehrt. Das Umschalten passiert sofort via `v-if`, kein Reload.

## Backend-Seite (Python)

Das Python-Backend nimmt bereits einen `provider`-Parameter entgegen. Prüfen, ob auch `model` angenommen wird. Falls nicht, Endpunkt entsprechend erweitern.

Für neue Provider (Gemini, Claude, Mistral) müssen Platzhalter für die API-Keys in der `.env`-Datei und in `config.py` angelegt werden, auch wenn die Implementierung noch fehlt. So ist der Weg vorbereitet.

## Zu testen

- Dropdown für Provider zeigt OpenAI, DeepSeek.
- Dropdown für Modell aktualisiert sich wenn Provider wechselt.
- Dropdown für Modus vorhanden (Chat, Ideen).
- Bei "Ideen"-Auswahl: Chat-Fenster verschwindet, Ideen-Formular erscheint.
- API-Request schickt `provider` und `model` korrekt ans Python-Backend.
