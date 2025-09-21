
<template>
  <div>
    <header>
      <h1 class="logo">nspace</h1>
      <nav>
        <button class="nav-btn" @click="$router.push('/')">Home</button>
        <button class="nav-btn" @click="$router.push('/profil')">Profil</button>
        <button class="nav-btn" @click="$router.push('/java')">Termine Java</button>
        <button class="nav-btn" @click="$router.push('/python')">KI Python</button>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
};
</script>

<style>
header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #eee;
}
nav {
  display: flex;
  gap: 8px;
}
main {
  padding: 16px;
}
button {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: #f7f7f7;
  border-radius: 6px;
  cursor: pointer;
}
button:hover {
  background: #efefef;
}
/* Logo im Wortmarken-Stil */
.logo {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 0.32em;
  text-transform: uppercase;
  color: #111;
}
/* Schlanke Buttons für die Navigation */
.nav-btn {
  appearance: none;
  border: 1px solid rgba(0,0,0,0.25);
  background: transparent;
  color: #111;
  padding: 10px 14px;
  border-radius: 999px;
  font-weight: 700;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: background 0.18s ease, color 0.18s ease, border-color 0.18s ease, transform 0.12s ease;
}
.nav-btn:hover {
  background: #111;
  color: #fff;
  border-color: #111;
  transform: translateY(-1px);
}

.nav-btn:active {
  transform: translateY(0);
}
</style>
