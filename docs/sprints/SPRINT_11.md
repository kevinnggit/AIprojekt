# Sprint Plan: SPRINT-11
**Motto:** "Security, Intelligence & Control"
**Goal:** Professionalize the platform with industrial-grade security (Cookies), advanced AI controls, and deep system visibility.

## 🎯 Sprint Goal
Wir härten die Anwendung gegen Angriffe (Cookie-Auth, HTTPS-Prep), geben dem Admin volle Transparenz (Activity Logs) und dem User volle Kontrolle über die KI (Multi-Model UI).

## 📋 Ticket Selection

### 1. Admin Security Refactor (SEC-001)
- **Status:** Critical
- **Todo:**
    - [Backend] Switch from Header-Token to **HttpOnly Cookies**.
    - [Frontend] Implement Route Guards (Redirect to login if no cookie).
    - [Frontend] Remove Token from LocalStorage.

### 2. AI UI Refactor (AI-002)
- **Status:** New Requirement
- **Todo:**
    - [Frontend] Dynamisches Menü: Provider (OpenAI/DeepSeek/Ollama/gemini/claude/Mistral) -> Model -> Task.
    - [Frontend] Task-Switching: "Chat" vs. "Project Generator" (Formulare ein-/ausblenden).
    - [Backend] Ensure Python backend accepts these params.
    - [Backend] stelle sicher dass die Parameter in der API angenommen werden und dass ein vorgesehener platzhalter für jede API-Key existiert(Wo ich die Keys speichern soll).

### 3. Admin Extensions: Activity & Portfolio (ADM-003)
- **Status:** Enhancement
- **Todo:**
    - [Frontend] "Dashboard"-Overview Tab mit Live-Stats (Termine heute, Errors, API Calls).
    - [Backend] Simple "Activity Log" oder Stats-Endpoint (`/api/admin/stats`).
    - [Portfolio] Review Existing Implementation (Polishing).

### 4. Infrastructure & HTTPS (INF-001)
- **Status:** Planning/Docs
- **Todo:**
    - Prepare Doc "How to enable HTTPS" (Nginx Proxy).
    - Hardening `docker-compose.yml`.

## 📅 Schedule
- **Day 1:** Security Refactor (Cookie Logic is tricky).
- **Day 2:** AI Refactor (Vue Components).
- **Day 3:** Admin Stats & Documentation.

## 📦 Deliverables
- **Secure Auth:** No more JWT in LocalStorage.
- **Improved AI UI:** Dropdown for Models.
- **Admin Stats:** A view showing system health.
