<template>
  <div class="login-container">
    <h1>Admin Login</h1>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label>Username</label>
        <input v-model="username" type="text" required />
      </div>
      <div class="form-group">
        <label>Password</label>
        <input v-model="password" type="password" required />
      </div>
      <button type="submit" :disabled="loading">Login</button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
  </div>
</template>

<script setup>
// Composition API: Wir importieren 'ref', um reaktive Variablen zu erzeugen.
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

// 'ref' bedeutet: Wenn sich dieser Wert ändert, aktualisiert Vue das HTML automatisch (Reactivity).
// Im HTML nutzen wir 'v-model', um Input-Felder direkt mit diesen Variablen zu verknüpfen.
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')
const authStore = useAuthStore()


const handleLogin = async () => {
    loading.value = true
    error.value = ''
    try {
        await authStore.login(username.value, password.value)
    } catch (e) {
        error.value = 'Login failed. Please check credentials.'
        console.error(e)
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-container { max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #333; border-radius: 8px; background: #1a1a1a; color: white; }
.form-group { margin-bottom: 15px; }
label { display: block; margin-bottom: 5px; }
input { width: 100%; padding: 8px; background: #333; border: 1px solid #555; color: white; }
button { width: 100%; padding: 10px; background: #4CAF50; color: white; border: none; cursor: pointer; }
.error { color: #ff6b6b; margin-top: 10px; }
</style>
