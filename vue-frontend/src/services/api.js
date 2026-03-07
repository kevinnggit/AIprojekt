/**
 * services/api.js – Zentrale HTTP-Schnittstelle der Anwendung
 *
 * Kapselt alle Netzwerkaufrufe an das Java-Backend (Port 8081) und
 * das Python-KI-Backend (Port 8000). Durch diese Bündelung müssen bei
 * einer URL-Änderung nur die Konstanten am Dateianfang angepasst werden.
 * Geschützte Endpunkte werden mit dem JWT-Token aus dem localStorage
 * über einen Authorization-Bearer-Header abgesichert.
 */

// Zentrale API-Definition
// Nutzt Environment-Variablen oder Fallbacks für lokale Entwicklung

const JAVA_API_URL = import.meta.env.VITE_JAVA_API_URL || 'http://localhost:8081';
const PYTHON_API_URL = import.meta.env.VITE_PYTHON_API_URL || 'http://localhost:8000';

console.log('API Config:', { JAVA_API_URL, PYTHON_API_URL });

// Zentrales API-Objekt.
// Vorteil: Wenn wir Backend-URLs ändern, müssen wir das nur hier tun.
// Komponenten rufen nur "api.termine.getAll()" auf und müssen nichts von HTTP wissen.
export const api = {

    // --- Termine (Java-Backend) ---
    termine: {
        /**
         * Ruft alle gespeicherten Termine vom Java-Backend ab.
         * @returns {Promise<Array>} Liste aller Termine als JSON
         */
        async getAll() {
            const res = await fetch(`${JAVA_API_URL}/api/termine`);
            if (!res.ok) throw new Error(`Java API Error: ${res.statusText}`);
            return res.json();
        },

        /**
         * Legt einen neuen Termin an.
         * @param {Object} appointment - Terminobjekt mit allen erforderlichen Feldern
         * @returns {Promise<Object>} Erstellter Termin inkl. serverseitig vergebener ID
         */
        async create(appointment) {
            const res = await fetch(`${JAVA_API_URL}/api/termine`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(appointment)
            });
            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.error || `Java API Error: ${res.statusText}`);
            }
            return res.json();
        },

        /**
         * Bestätigt einen Termin anhand seiner ID (erfordert Admin-Token).
         * @param {number|string} id - ID des zu bestätigenden Termins
         * @returns {Promise<Object>} Aktualisiertes Terminobjekt
         */
        async confirm(id) {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/termine/${id}/confirm`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            if (!res.ok) throw new Error(`Java API Error: ${res.statusText}`);
            return res.json();
        },

        /**
         * Löscht einen Termin anhand seiner ID (erfordert Admin-Token).
         * @param {number|string} id - ID des zu löschenden Termins
         */
        async delete(id) {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/termine/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!res.ok) throw new Error(`Java API Error: ${res.statusText}`);
        },

        /**
         * Lädt die Terminbuchungs-Konfiguration (z. B. verfügbare Zeitfenster).
         * @returns {Promise<Object>} Konfigurationsobjekt
         */
        async getConfig() {
            const res = await fetch(`${JAVA_API_URL}/api/termine/config`);
            if (!res.ok) throw new Error(`Java API Error: ${res.statusText}`);
            return res.json();
        }
    },

    // --- KI-Funktionen (Python-Backend) ---
    ai: {
        /**
         * Sendet einen Text zur KI-Inferenz an das Python-Backend.
         * @param {string} text - Eingabetext für das Sprachmodell
         * @param {string} [provider='openai'] - Zu verwendender KI-Anbieter
         * @returns {Promise<Object>} Antwort des Modells
         */
        async infer(text, provider = 'openai') {
            const res = await fetch(`${PYTHON_API_URL}/api/ki/infer`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ text, provider })
            });
            if (!res.ok) throw new Error(`Python API Error: ${res.statusText}`);
            return res.json();
        },

        /**
         * Generiert eine festgelegte Anzahl an Ideen zu einem Thema.
         * @param {string} topic - Thema, zu dem Ideen generiert werden sollen
         * @param {number} [count=3] - Gewünschte Anzahl an Ideen
         * @param {string} [provider='openai'] - Zu verwendender KI-Anbieter
         * @returns {Promise<Object>} Generierte Ideen als strukturiertes Objekt
         */
        async generateIdeas(topic, count = 3, provider = 'openai') {
            const res = await fetch(`${PYTHON_API_URL}/api/ki/generate-ideas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ topic, count, provider })
            });
            if (!res.ok) throw new Error(`Python API Error: ${res.statusText}`);
            return res.json();
        }
    },

    // --- Authentifizierung (Java-Backend) ---
    auth: {
        /**
         * Übermittelt Anmeldedaten und empfängt bei Erfolg einen JWT-Token.
         * @param {string} username - Benutzername
         * @param {string} password - Passwort
         * @returns {Promise<Object>} Antwortobjekt mit token-Feld
         */
        async login(username, password) {
            const res = await fetch(`${JAVA_API_URL}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            if (!res.ok) throw new Error(`Auth Error: ${res.statusText}`);
            return res.json();
        }
    },

    // --- Admin-Konfiguration (Java-Backend, Token erforderlich) ---
    admin: {
        /**
         * Lädt alle Konfigurationseinträge aus der Admin-API.
         * @returns {Promise<Array>} Liste der Konfigurationsschlüssel-Wert-Paare
         */
        async getConfig() {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/config`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) throw new Error(`Fetch Config Error: ${res.statusText}`);
            return res.json();
        },

        /**
         * Schreibt oder aktualisiert einen einzelnen Konfigurationseintrag.
         * @param {string} key - Konfigurationsschlüssel
         * @param {string} value - Neuer Wert
         * @param {string} description - Beschreibung des Eintrags
         */
        async updateConfig(key, value, description) {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/config`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ key, value, description })
            });
            if (!res.ok) throw new Error(`Update Config Error: ${res.statusText}`);
        },

        /**
         * Ruft alle registrierten Nutzerkonten ab.
         * @returns {Promise<Array>} Liste der Nutzer
         */
        async getUsers() {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/users`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) throw new Error(`Fetch Users Error: ${res.statusText}`);
            return res.json();
        },

        /**
         * Legt ein neues Nutzerkonto an.
         * @param {string} username - Benutzername des neuen Kontos
         * @param {string} password - Initiales Passwort
         * @param {string} role - Rolle, z. B. 'ROLE_USER' oder 'ROLE_ADMIN'
         * @returns {Promise<Object>} Neu erstelltes Nutzerobjekt
         */
        async createUser(username, password, role) {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/users`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password, role })
            });
            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.message || `Create User Error: ${res.statusText}`);
            }
            return res.json();
        }
    },

    // --- Portfolio-Einträge (Java-Backend) ---
    portfolio: {
        /**
         * Ruft alle öffentlichen Portfolio-Einträge ab.
         * @returns {Promise<Array>} Liste der Portfolio-Projekte
         */
        async getAll() {
            const res = await fetch(`${JAVA_API_URL}/api/portfolio`);
            if (!res.ok) throw new Error(`Portfolio Error: ${res.statusText}`);
            return res.json();
        },

        /**
         * Erstellt einen neuen Portfolio-Eintrag (erfordert Admin-Token).
         * @param {Object} item - Portfolio-Objekt mit Titel, Beschreibung usw.
         * @returns {Promise<Object>} Erstellter Eintrag inkl. serverseitig vergebener ID
         */
        async create(item) {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/portfolio`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(item)
            });
            if (!res.ok) throw new Error(`Create Portfolio Error: ${res.statusText}`);
            return res.json();
        },

        /**
         * Löscht einen Portfolio-Eintrag anhand seiner ID (erfordert Admin-Token).
         * @param {number|string} id - ID des zu löschenden Eintrags
         */
        async delete(id) {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/portfolio/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) throw new Error(`Delete Portfolio Error:${res.statusText}`);
        }
    }
};
