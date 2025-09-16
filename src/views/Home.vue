<template>
    <section class="home">
      <div class="backdrop"></div>
      <div class="content">
        <h1>Erkunde das Unbekannte</h1>
        <p>Ich bin Kevin. Student, Entwickler, IT-Projektmanager – hier findest du Projekte, Technologien und Visionen.</p>
        <div class="cta">
          <button @click="$router.push('/profil')">Profil ansehen</button>
          <button class="ghost" @click="$router.push('/java')">Termine Java</button>
          <button class="ghost" @click="$router.push('/python')">KI Python</button>
        </div>
      </div>
      <div class="footer-note">
        <span>Scroll</span>
        <div class="mouse">
          <div class="wheel"></div>
        </div>
      </div>
    </section>
  </template>
  
  <script>
  export default {
    name: 'HomeView'
  };
  </script>
  
  <style scoped>
  .home {
    position: relative;
    min-height: 100vh;
    width: 100%;
    color: #fff;
    overflow: hidden;
    background: #000;
  }
  
  .backdrop {
    position: absolute;
    inset: 0;
    background:
      radial-gradient(1200px 600px at 70% -10%, rgba(255,255,255,0.08), transparent 60%),
      radial-gradient(900px 500px at 20% 10%, rgba(255,255,255,0.06), transparent 60%),
      radial-gradient(1600px 800px at 50% 120%, rgba(255,255,255,0.07), transparent 60%),
      #000;
  }
  .backdrop::after {
    content: "";
    position: absolute;
    inset: 0;
    background:
      radial-gradient(2px 2px at 20% 30%, rgba(255,255,255,0.9), transparent 60%),
      radial-gradient(1.5px 1.5px at 40% 70%, rgba(255,255,255,0.8), transparent 60%),
      radial-gradient(1.5px 1.5px at 60% 20%, rgba(255,255,255,0.7), transparent 60%),
      radial-gradient(2px 2px at 80% 50%, rgba(255,255,255,0.85), transparent 60%),
      radial-gradient(1px 1px at 30% 85%, rgba(255,255,255,0.7), transparent 60%),
      radial-gradient(1px 1px at 75% 80%, rgba(255,255,255,0.6), transparent 60%);
    opacity: 0.7;
    animation: drift 30s linear infinite;
  }
  
  @keyframes drift {
    0%   { transform: translateY(0px); }
    100% { transform: translateY(-40px); }
  }
  
  .content {
    position: relative;
    z-index: 1;
    max-width: 1100px;
    margin: 0 auto;
    padding: 20vh 24px 0 24px;
    text-align: left;
  }
  
  h1 {
    font-size: clamp(36px, 7vw, 84px);
    line-height: 1.02;
    letter-spacing: 0.02em;
    text-transform: uppercase;
    margin: 0 0 16px 0;
    font-weight: 800;
  }
  
  p {
    font-size: clamp(16px, 2.2vw, 20px);
    line-height: 1.6;
    max-width: 720px;
    color: rgba(255,255,255,0.9);
    margin: 0 0 28px 0;
  }
  
  .cta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
  
  button {
    appearance: none;
    border: none;
    background: #fff;
    color: #000;
    padding: 12px 18px;
    border-radius: 999px;
    font-weight: 700;
    letter-spacing: 0.02em;
    cursor: pointer;
    transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.2s ease, color 0.2s ease;
  }
  button:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 24px rgba(255,255,255,0.12);
  }
  button:active {
    transform: translateY(0);
  }
  
  button.ghost {
    background: transparent;
    color: #fff;
    border: 1px solid rgba(255,255,255,0.35);
  }
  button.ghost:hover {
    background: rgba(255,255,255,0.08);
    box-shadow: none;
  }
  
  .footer-note {
    position: absolute;
    left: 24px;
    bottom: 24px;
    z-index: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    color: rgba(255,255,255,0.85);
    letter-spacing: 0.1em;
    text-transform: uppercase;
    font-size: 12px;
  }
  
  .mouse {
    width: 22px;
    height: 36px;
    border: 2px solid rgba(255,255,255,0.7);
    border-radius: 14px;
    display: grid;
    place-items: start center;
    padding-top: 6px;
  }
  .wheel {
    width: 2px;
    height: 6px;
    background: rgba(255,255,255,0.9);
    border-radius: 1px;
    animation: wheel 1.6s ease-in-out infinite;
  }
  @keyframes wheel {
    0% { opacity: 0; transform: translateY(-4px); }
    30% { opacity: 1; transform: translateY(0px); }
    70% { opacity: 1; transform: translateY(6px); }
    100% { opacity: 0; transform: translateY(12px); }
  }
  
  @media (min-width: 900px) {
    .content { padding-top: 22vh; }
  }
  </style>