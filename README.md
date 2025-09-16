# NSPACE - Portfolio Website

Eine moderne Portfolio-Website im NSPACE-Design, entwickelt mit Vue 3 und Vite.

## 🚀 Features

- **Design**: Dunkles Hero-Layout mit animierten Sterneffekten
- **Vue 3 + Vite**: Moderne Frontend-Architektur mit Hot Module Replacement
- **Vue Router**: Single Page Application mit Navigation zwischen 4 Hauptbereichen
- **Responsive Design**: Optimiert für Desktop und Mobile
- **NSPACE Branding**: Minimalistisches Logo und Navigation

## 📁 Projektstruktur

```
src/
├── App.vue              # Hauptkomponente mit Navigation
├── main.js              # App-Entry-Point
├── router/
│   └── index.js         # Vue Router Konfiguration
└── views/
    ├── Home.vue         # Hero-Seite mit SpaceX-Design
    ├── Profile.vue      # Profil-Informationen
    ├── TermineJava.vue  # Java-Termine Bereich
    └── KiPython.vue     # KI Python Bereich
```

## 🛠️ Setup & Installation

### Voraussetzungen

- **Node.js**: Version 20.19.0 oder höher (oder 22.12.0+)
- **npm**: Package Manager (kommt mit Node.js)

### Installation

1. **Repository klonen/herunterladen**
   ```bash
   git clone [repository-url]
   cd AIprojekt
   ```

2. **Abhängigkeiten installieren**
   ```bash
   npm install
   ```

3. **Entwicklungsserver starten**
   ```bash
   npm run dev
   ```

4. **Browser öffnen**
   - Öffne http://localhost:5173 (oder den angezeigten Port)

### Build für Produktion

```bash
# Produktionsbuild erstellen
npm run build

# Build-Vorschau starten
npm run preview
```

## 🎨 Design & Features

### Home-Seite (SpaceX-Style)
- **Fullscreen Hero**: Dunkler Hintergrund mit animierten Lichtkegeln
- **Sterneffekt**: CSS-basierte Partikel-Animation
- **Call-to-Action Buttons**: Direkte Navigation zu Profil, Java, Python
- **Scroll-Indikator**: Animierte Maus mit Scroll-Hinweis

### Navigation
- **NSPACE Logo**: Minimalistische Wortmarke
- **Outline-Buttons**: Elegante Hover-Effekte im SpaceX-Stil
- **Responsive**: Funktioniert auf allen Bildschirmgrößen

### Seiten
- **Home**: Hero-Landing mit CTA-Buttons
- **Profil**: Persönliche Informationen (Kevin Nguefack)
- **Termine Java**: Platzhalter für Java-bezogene Termine
- **KI Python**: Platzhalter für KI/Python-Projekte

## 🔧 Technische Details

### Dependencies
- **Vue 3.5.18**: Progressive JavaScript Framework
- **Vue Router 4.5.1**: Offizielle Router-Lösung für Vue.js
- **Vite 7.0.6**: Schneller Build-Tool und Dev-Server

### Dev Dependencies
- **@vitejs/plugin-vue**: Vue.js Support für Vite
- **vite-plugin-vue-devtools**: Vue DevTools Integration

### Konfiguration
- **ES Modules**: `"type": "module"` in package.json
- **Path Alias**: `@` → `./src` für saubere Imports
- **Vue DevTools**: Aktiviert für bessere Entwicklungserfahrung

## 🚀 Was bereits implementiert wurde

✅ **Vue 3 + Vite Setup**  
✅ **Vue Router Konfiguration**  
✅ **4 Hauptseiten mit Navigation**  
✅ **SpaceX-inspiriertes Hero-Design**  
✅ **Responsive Navigation mit NSPACE Branding**  
✅ **Animierte Sterneffekte und Hover-Interaktionen**  
✅ **Produktions-Build konfiguriert**  

## 📝 Nächste Schritte

- [ ] Profil-Seite mit echten Informationen ausbauen
- [ ] Java-Termine Funktionalität implementieren
- [ ] KI Python Projekte/Portfolio hinzufügen
- [ ] Kontakt-Formular oder Social Links
- [ ] SEO-Optimierung (Meta-Tags, etc.)
- [ ] Performance-Optimierung (Lazy Loading, etc.)

## 🎯 Verwendung

1. **Entwicklung**: `npm run dev` startet den Dev-Server
2. **Navigation**: Über die Buttons in der oberen Navigation
3. **Anpassung**: Inhalte in den entsprechenden `.vue` Dateien bearbeiten
4. **Styling**: CSS in den `<style>` Blöcken der Komponenten

## 📞 Support

Bei Fragen oder Problemen:
- Node.js Version prüfen: `node --version`
- Dependencies neu installieren: `rm -rf node_modules && npm install`
- Dev-Server neu starten: `Ctrl+C` dann `npm run dev`

---

**Entwickelt mit ❤️ und Vue 3**