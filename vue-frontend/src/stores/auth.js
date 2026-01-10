import { defineStore } from 'pinia'
import { api } from '@/services/api'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        user: null
    }),
    getters: {
        isAuthenticated: (state) => !!state.token,
        isAdmin: (state) => state.token && getRoleFromToken(state.token) === 'ROLE_ADMIN'
    },
    actions: {
        async login(username, password) {
            try {
                const response = await api.auth.login(username, password)
                // Adjust based on your API response structure. 
                // Assuming response.data.token or just response if interceptor handles it.
                // My api.js uses fetch/axios? Let's check api.js later. Assuming it returns parsed JSON.
                const token = response.token
                this.token = token
                localStorage.setItem('token', token)

                // Decode token to get user info if needed, or fetch user
                router.push('/admin')
            } catch (error) {
                throw error
            }
        },
        logout() {
            this.token = null
            this.user = null
            localStorage.removeItem('token')
            router.push('/login')
        }
    }
})

function getRoleFromToken(token) {
    if (!token) return null;
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        // The claim might be "role" or "authorities" depending on JwtUtil
        // In JwtUtil I put: claims.put("role", userDetails.getAuthorities().toString());
        // e.g. "[ROLE_USER]"
        let roleRole = payload.role;
        if (roleRole && roleRole.includes('ROLE_ADMIN')) return 'ROLE_ADMIN';
        return 'ROLE_USER';
    } catch (e) {
        return null;
    }
}
