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
            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.error || `Java API Error: ${res.statusText}`);
            }
            return res.json();
        },
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
        async getConfig() {
            const res = await fetch(`${JAVA_API_URL}/api/termine/config`);
            if (!res.ok) throw new Error(`Java API Error: ${res.statusText}`);
            return res.json();
        }
    },
    ai: {
        async infer(text, provider = 'openai') {
            const res = await fetch(`${PYTHON_API_URL}/api/ki/infer`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ text, provider })
            });
            if (!res.ok) throw new Error(`Python API Error: ${res.statusText}`);
            return res.json();
        },
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
    },
    admin: {
        async getConfig() {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/config`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) throw new Error(`Fetch Config Error: ${res.statusText}`);
            return res.json();
        },
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
        async getUsers() {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/users`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) throw new Error(`Fetch Users Error: ${res.statusText}`);
            return res.json();
        },
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
    portfolio: {
        async getAll() {
            const res = await fetch(`${JAVA_API_URL}/api/portfolio`);
            if (!res.ok) throw new Error(`Portfolio Error: ${res.statusText}`);
            return res.json();
        },
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
        async delete(id) {
            const token = localStorage.getItem('token');
            const res = await fetch(`${JAVA_API_URL}/api/admin/portfolio/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) throw new Error(`Delete Portfolio Error: ${res.statusText}`);
        }
    }
};
