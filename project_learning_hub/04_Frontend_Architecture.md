# 04. Frontend Architektur: Das Gesicht (Sprint 4)

Unser Frontend ist nicht nur "HTML mit bisschen Klick-Bunti". Es ist eine hochmoderne **Single Page Application (SPA)** basierend auf **Vue.js 3**.

## 1. Vue.js Konzepte (Composition API)

Wir nutzen die neue **Composition API** (`<script setup>`). Das macht den Code logischer und besser typisierbar als die alte Options API.

### Reactivity: Der Kern von Vue
Wie aktualisiert sich die Webseite automatisch?

```javascript
import { ref } from 'vue';

const count = ref(0); // "ref" macht die Variable "reaktiv"
```
Wenn wir `count.value` im Code ändern, merkt Vue das und aktualisiert SOFORT den Teil der HTML-Seite, wo `{{ count }}` steht. Das nennt man "Reactivity".

### Two-Way Binding (`v-model`)
In Formularen wollen wir Daten lesen UND schreiben.

```html
<input v-model="username" />
```
-   Tippt der User etwas, landet es in der Variable `username`.
-   Ändern wir `username` im Code, ändert sich der Text im Input-Feld.
Das ist **Two-Way Data Binding**.

## 2. Kommunikation mit Backends (CORS & Ports)

Unser Frontend läuft auf Port `3000`. Die Backends auf `8080` (Java) und `8000` (Python).
Browser verbieten aus Sicherheit, dass `site-a.com` Daten von `site-b.com` lädt. Das nennt man **Same-Origin Policy**.

### Die Lösung: CORS
Unsere Backends müssen explizit sagen: "Ja, der Kollege von Port 3000 darf meine Daten sehen."
Das haben wir in Spring (`@CrossOrigin`) und FastAPI (`CORSMiddleware`) konfiguriert.

## 3. Architektur-Pattern: API Service (`api.js`)

Wir schreiben NIEMALS `fetch(...)` direkt in einen Button-Click Handler.
Warum?
1.  **Redundanz:** Wir wollen die URL `http://localhost:8080` nicht 50x kopieren.
2.  **Wartbarkeit:** Wenn sich der Endpunkt ändert, ändern wir es nur an einer Stelle.

### Clean Code Beispiel

**Schlecht (im Component):**
```javascript
async function login() {
  await fetch('http://localhost:8080/api/auth/login', ...) // Hardcoded & unwartbar
}
```

**Gut (`api.js` + Component):**
```javascript
// api.js
export const api = {
  auth: { login: (u, p) => fetch(`${BASE_URL}/login`, ...) }
}

// Component
import { api } from '@/services/api';
await api.auth.login(user, pw);
```
Das trennt die **View** (Anzeige) von der **Infrastruktur** (Datenladen).
