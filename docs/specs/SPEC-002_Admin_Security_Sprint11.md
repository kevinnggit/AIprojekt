# Admin-Sicherheit: Cookie-Auth und Route Guards

**Feature:** SEC-001
**Sprint:** 08

## Was sich ändern soll

Aktuell speichern wir das JWT im LocalStorage und schicken es als `Authorization: Bearer`-Header mit. Das ist ein XSS-Risiko — jedes injizierte Script kann den Token stehlen und sich als Admin ausgeben.

Ziel: JWT in einem HttpOnly Cookie, das der Browser automatisch mitschickt und auf das JavaScript keinen Zugriff hat.

## Backend-Änderungen (Java)

**Login-Response anpassen:**
1. `AuthController.login()` setzt statt dem Token im Body jetzt ein Cookie:
    - `Set-Cookie: jwt_token=...; HttpOnly; Path=/; Max-Age=3600; SameSite=Strict`
    - Der Response-Body enthält nur noch Nutzerinfos (Rolle, Name), aber kein Token.
2. `JwtAuthenticationFilter` liest das Token aus dem Cookie statt aus dem Header.
    - Als Fallback bleibt der Header-Check erhalten — so funktioniert das Testen mit Postman weiterhin.

**Logout:**
- `POST /api/auth/logout` löscht das Cookie indem `Max-Age=0` gesetzt wird.

## Frontend-Änderungen (Vue)

**Auth-Store (`stores/auth.js`):**
- `localStorage.setItem('token', ...)` komplett entfernen.
- Nach erfolgreichem Login: `this.user = response.user` (Nutzerinfos aus dem Response-Body).
- `isAuthenticated` basiert nicht mehr auf dem Token aus LocalStorage, sondern auf einem `/api/auth/me`-Aufruf beim App-Start.

**Neuer Endpunkt nötig:** `/api/auth/me` — gibt Nutzerinfos zurück wenn das Cookie gültig ist, sonst 401. Damit weiß das Frontend nach einem Reload, ob die Session noch läuft.

**Route Guards (`router/index.js`):**
```javascript
router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore();
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
      // Versuch Session wiederherzustellen via /me endpoint
      try { await auth.checkSession(); } catch { return next('/login'); }
  }
  next();
});
```

## Zu testen

- Direktaufruf von `/admin` ohne Login muss auf `/login` weiterleiten.
- Nach erfolgreichem Login darf kein Token im LocalStorage stehen.
- API-Requests müssen weiterhin funktionieren.
- Nach Seiten-Reload muss der Login-Status erhalten bleiben (weil das Cookie auf dem Browser liegt).
