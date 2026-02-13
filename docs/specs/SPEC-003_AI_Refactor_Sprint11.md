# Specification: AI UI Refactor

**Feature ID:** AI-002
**Sprint:** 08

## User Story
Als User möchte ich über ein zentrales Menü steuern, was ich von der KI will (Chatten, Bilder malen, Ideen finden) und welches "Gehirn" (Modell) ich dafür nutze, damit das Interface nicht mit ungenutzten Feldern überladen ist.

## Requirements

### 1. Provider & Model Selection
Das Auswahlmenü muss hierarchisch sein (oder smart):
- **Provider:** OpenAI, DeepSeek, (Mock)
- **Model:** Abhängig vom Provider (z.B. OpenAI -> gpt-4o, gpt-3.5-turbo; DeepSeek -> v3)

### 2. Task Selection (Der "Modus")
Dropdown/Tabs für "Aufgabe":
1.  **Chat / Analyse** (Standard) -> Zeigt Textfeld für Input.
2.  **Projekt-Ideen** -> Zeigt Formular für Thema + Anzahl.
3.  **Image Gen** (Placeholder) -> Zeigt Prompt-Feld.

### 3. Dynamic UI
- Wenn "Chat" gewählt ist, darf das "Projekt-Ideen"-Formular NICHT sichtbar sein.
- Die UI muss sich *sofort* ändern (Conditional Rendering `v-if`).

### 4. Technical Constraints
- Backend muss dynamisch entscheiden, welchen Provider es nutzt (wurde in Sprint 7 vorbereitet?).
- Frontend muss die Auswahl (Provider + Model) bei jedem Request mitschicken.

## Acceptance Criteria
- [ ] Es gibt ein Dropdown für "Provider" (OpenAI, DeepSeek).
- [ ] Es gibt ein Dropdown für "Model" (ändert sich je nach Provider).
- [ ] Es gibt ein Dropdown für "Task" (Chat, Ideen).
- [ ] Wähle ich "Ideen", verschwindet das Chat-Fenster und das Ideen-Formular erscheint.
- [ ] Der API-Request sendet `provider` und `model` Parameter korrekt an das Python-Backend.
