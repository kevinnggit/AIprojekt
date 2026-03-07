/**
 * stores/auth.js – Pinia Store für Authentifizierung
 *
 * Verwaltet den JWT-Token und den Authentifizierungsstatus des Nutzers.
 * Der Token wird im localStorage persistiert, damit die Sitzung bei einem
 * Seitenneuladen erhalten bleibt.
 */
import { defineStore } from 'pinia'
import { api } from '@/services/api'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        // Token beim Start aus dem localStorage wiederherstellen
        token: localStorage.getItem('token') || null,
        user: null
    }),
    getters: {
        // Gibt true zurück, wenn ein Token vorhanden ist
        isAuthenticated: (state) => !!state.token,
        // Prüft anhand des JWT-Payloads, ob die Rolle ROLE_ADMIN vorliegt
        isAdmin: (state) => state.token && getRoleFromToken(state.token) === 'ROLE_ADMIN'
    },
    actions: {
        /**
         * Meldet den Nutzer an und speichert den erhaltenen JWT-Token.
         * Bei Erfolg erfolgt eine automatische Weiterleitung zum Admin-Bereich.
         * @param {string} username - Benutzername
         * @param {string} password - Passwort im Klartext (wird nur über HTTPS übertragen)
         */
        async login(username, password) {
            try {
                const response = await api.auth.login(username, password)
                // Adjust based on your API response structure.
                // Assuming response.data.token or just response if interceptor handles it.
                // My api.js uses fetch/axios? Let's check api.js later. Assuming it returns parsed JSON.
                const token = response.token
                this.token = token
                // Token dauerhaft im Browser speichern
                localStorage.setItem('token', token)

                // Decode token to get user info if needed, or fetch user
                router.push('/admin')
            } catch (error) {
                throw error
            }
        },
        /**
         * Meldet den aktuellen Nutzer ab, löscht Token und Nutzerdaten
         * und leitet zur Login-Seite weiter.
         */
        logout() {
            this.token = null
            this.user = null
            // Token aus dem persistenten Speicher entfernen
            localStorage.removeItem('token')
            router.push('/login')
        }
    }
})

/**
 * Dekodiert den JWT-Payload (Base64) und extrahiert die Rolle des Nutzers.
 * Es wird keine kryptografische Signaturprüfung durchgeführt –
 * die Validierung obliegt ausschließlich dem Backend.
 * @param {string} token - JWT-Token im Format header.payload.signature
 * @returns {string|null} Rolle ('ROLE_ADMIN', 'ROLE_USER') oder null bei Fehler
 */
function getRoleFromToken(token) {
    if (!token) return null;
    try {
        // Mittelteil (Payload) des JWT dekodieren
        const payload = JSON.parse(atob(token.split('.')[1]));
        // The claim might be "role" or "authorities" depending on JwtUtil
        // In JwtUtil I put: claims.put("role", userDetails.getAuthorities().toString());
        // e.g. "[ROLE_USER]"
        let roleRole = payload.role;
        if (roleRole && roleRole.includes('ROLE_ADMIN')) return 'ROLE_ADMIN';
        return 'ROLE_USER';
    } catch (e) {
        // Ungültiger oder manipulierter Token – sicher abfangen
        return null;
    }
}
