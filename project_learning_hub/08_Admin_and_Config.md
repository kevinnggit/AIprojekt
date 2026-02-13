# 08. Admin Power & Configuration (Sprint 8)

Willkommen im Kontrollzentrum.
Wir haben das System von "hardcoded" auf "dynamisch" umgestellt.
Admins können jetzt Systemeinstellungen zur Laufzeit ändern, ohne den Server neu zu starten.

## 1. Dynamic Configuration Pattern
Problem: Wir hatten Konfigurationen fest im Java-Code:
```java
// Schlecht: Änderung erfordert Neukompilierung & Redeploy
final static int MAX_BOOKING_MONTHS = 3;
```

Lösung: **Database-Driven Configuration (`GlobalConfig`)**.
Wir speichern Key-Value Paare in der Datenbank (`global_config` Tabelle).

### Der `ConfigService` (Graceful Degradation)
Wenn die Datenbank leer ist (oder der Key fehlt), darf die App nicht crashen.
Deshalb nutzt unser Service das **Default-Value Pattern**:
```java
// "Hol mir 'booking_window', aber wenn nix da ist, nimm 3"
int months = configService.getInt("booking_window_months", 3);
```
Das garantiert absolute Stabilität.

## 2. Public vs. Private Config Endpoints
Wir haben zwei Wege, wie Konfiguration gelesen wird:

### A. Private (Admin Write)
`POST /api/admin/config`
Nur für Admins (`ROLE_ADMIN`). Hier werden Werte geändert.

### B. Public (Frontend Read)
`GET /api/termine/config`
Jeder darf wissen, wie weit im Voraus man buchen darf.
Das Vue-Frontend lädt diese Info beim Start, um den Kalender entsprechend einzuschränken (Buttons ausgrauen).
**Wichtig:** Wir exponieren nicht *alle* Configs, sondern nur ausgewählte *Safe-List* Werte (keine API-Keys!).

## 3. Frontend Architecture: Tabbed Dashboard
Da unser Admin-Dashboard wächst (Termine, Config, User), haben wir es refactored.
Statt einer riesigen Seite nutzen wir **Conditional Rendering** mit Tabs.

### `currentTab` State
```javascript
const currentTab = ref('termine'); // 'termine' | 'config' | 'users'
```
Im Template:
```html
<div v-if="currentTab === 'termine'">...</div>
<div v-if="currentTab === 'config'">...</div>
```

### Performance Optimierung: Smart Polling
Polling (alle 5s aktualisieren) ist teuer.
Wir wollen es nur, wenn der Admin die Termine auch *sieht*.
Wenn er im "Config"-Tab ist, pausieren wir das Polling.
 -> Das spart Server-Ressourcen und Netzwerk-Traffic.
