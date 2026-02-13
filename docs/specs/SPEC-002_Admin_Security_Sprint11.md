# Specification: Admin Security & Auth Refactor

**Feature ID:** SEC-001
**Sprint:** 08

## User Story
Als Administrator möchte ich, dass mein Login sicher ist und Sitzungen über Cookies verwaltet werden, um XSS-Angriffe zu erschweren, und dass unbefugte Nutzer keinen Zugriff auf Admin-Routen haben.

## Requirements

### 1. Route Guards (Frontend)
- Alle Routen unter `/admin` müssen geschützt sein.
- Wenn `isAuthenticated` false ist -> Redirect zu `/login`.
- Global Navigation Guard in `vue-router`.

### 2. Cookie Authentication (Backend/Frontend)
- **Status Quo:** Token im LocalStorage -> Header `Authorization: Bearer xyz`.
- **Target State:** Token im HttpOnly Cookie.

#### Flow
1.  **Login Request:** User sendet POST User/Pass.
2.  **Login Response:** Server setzt `Set-Cookie: jwt_token=...; HttpOnly; Path=/; Max-Age=3600; SameSite=Strict`. Body enthält user info (role, name), aber KEIN Token.
3.  **API Requests:** Browser sendet Cookie automatisch mit.
4.  **Backend Filter:** `JwtAuthenticationFilter` liest Token aus Cookie (statt Header).

#### Logout
- Endpoint `/api/auth/logout` löscht das Cookie (Max-Age=0).

## Technical Implementation Steps

### Backend (Java)
1.  Modify `AuthController.login`:
    - Erstelle `ResponseCookie`.
    - `response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())`.
2.  Modify `JwtRequestFilter`:
    - Check Request Cookies for `jwt_token`.
    - Fallback to Header (good for API testing via Postman).

### Frontend (Vue)
1.  UPDATE `auth.store`:
    - Remove `localStorage.setItem('token')`.
    - Login action calls API, then sets `this.user = response.user`.
    - `isAuthenticated` state relies on successful API calls (catch 401).
    - **WICHTIG:** Wir brauchen einen `/api/auth/me` Endpoint, den wir beim App-Start aufrufen, um zu prüfen, ob das Cookie noch gültig ist.

### Router
1.  In `router/index.js`:
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

## Acceptance Criteria
- [ ] Zugriff auf `/admin` ohne Login leitet auf `/login` um.
- [ ] Nach Login ist kein Token im LocalStorage zu finden.
- [ ] API Requests funktionieren weiterhin.
- [ ] Reload der Seite behält den Login-Status (persistiert durch Cookie).
