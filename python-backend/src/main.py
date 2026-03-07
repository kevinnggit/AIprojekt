"""
Einstiegspunkt der FastAPI-Anwendung für das NSPACE Python-Backend.
Initialisiert Datenbank, Logging, CORS-Middleware und registriert alle Router.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from src.routers.ai_router import router as ai_router
from src.config import ALLOWED_ORIGINS
from src.database import engine, Base
from src.models import audit  # Modelle registrieren, damit SQLAlchemy die Tabellen kennt

# Datenbanktabellen beim Start erstellen, falls sie noch nicht existieren
Base.metadata.create_all(bind=engine)

import logging
import os

# Log-Verzeichnis sicherstellen – in Docker per Volume gemountet, lokal wird es angelegt
os.makedirs("/app/logs", exist_ok=True)

# Logging konfigurieren: Ausgabe in Datei und gleichzeitig auf die Konsole
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    handlers=[
        logging.FileHandler("/app/logs/python-backend.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)
logger.info("Python Backend Startup")

# FastAPI-Instanz mit Metadaten für die automatisch generierte API-Dokumentation
app = FastAPI(
    title="NSPACE Python API",
    description="KI Python Backend for NSPACE Portfolio",
    version="1.0.0"
)

# CORS (Cross-Origin Resource Sharing)
# Wichtig: Erlaubt dem Frontend (localhost:3000) Anfragen an dieses Backend (localhost:8000).
# Ohne das würde der Browser die Verbindung blockieren (Sicherheitsfeature).
app.add_middleware(
    CORSMiddleware,
    allow_origins=ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
async def root():
    """Gibt eine einfache Statusmeldung zurück, um zu bestätigen, dass die API läuft."""
    return {"message": "NSPACE Python API is running", "version": "1.0.0"}


@app.get("/health")
async def health():
    """Health-Check-Endpunkt für Container-Orchestrierung (z.B. Docker, Kubernetes)."""
    return {"status": "healthy", "service": "python-backend"}


# KI-Router einbinden – alle Endpunkte unter /api/ki werden hier registriert
app.include_router(ai_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
