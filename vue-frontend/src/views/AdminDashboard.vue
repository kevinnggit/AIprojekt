<template>
  <div class="admin-dashboard">
    <h1>Admin Dashboard</h1>
    <div class="status-bar">
      Welcome, Admin.
      <button @click="authStore.logout()">Logout</button>
    </div>
    
    <div class="content">
      <p>Secure Area - Coming Soon: Appointment Management, Logs.</p>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
</script>

<style scoped>
.admin-dashboard { padding: 20px; color: white; }
.status-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #333; padding-bottom: 10px; }
button { background: #ff4444; color: white; border: none; padding: 5px 10px; cursor: pointer; }
</style>
