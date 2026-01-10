// Zentrale API-Definition
// Nutzt Environment-Variablen oder Fallbacks für lokale Entwicklung

const JAVA_API_URL = import.meta.env.VITE_JAVA_API_URL || 'http://localhost:8081';
const PYTHON_API_URL = import.meta.env.VITE_PYTHON_API_URL || 'http://localhost:8000';

console.log('API Config:', { JAVA_API_URL, PYTHON_API_URL });

// Zentrales API-Objekt.
// Vorteil: Wenn wir Backend-URLs ändern, müssen wir das nur hier tun.
// Komponenten rufen nur "api.termine.getAll()" auf und müssen nichts von HTTP wissen.
export const api = {
    termine: {
        async getAll() {
            const res = await fetch(`${JAVA_API_URL}/api/termine`);
            if (!res.ok) throw new Error(`Java API Error: ${res.statusText}`);
            return res.json();
        },
        async create(appointment) {
            const res = await fetch(`${JAVA_API_URL}/api/termine`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(appointment)
            });
            if (!res.ok) throw new Error(`Java API Error: ${res.statusText}`);
            return res.json();
        }
    },
    ai: {
        async infer(text) {
            const res = await fetch(`${PYTHON_API_URL}/api/ki/infer`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ text })
            });
            if (!res.ok) throw new Error(`Python API Error: ${res.statusText}`);
            return res.json();
        },
        async generateIdeas(topic, count = 3) {
            const res = await fetch(`${PYTHON_API_URL}/api/ki/generate-ideas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ topic, count })
            });
            if (!res.ok) throw new Error(`Python API Error: ${res.statusText}`);
            return res.json();
        }
    },
    auth: {
        async login(username, password) {
            const res = await fetch(`${JAVA_API_URL}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            if (!res.ok) throw new Error(`Auth Error: ${res.statusText}`);
            return res.json();
        }
    }
};
