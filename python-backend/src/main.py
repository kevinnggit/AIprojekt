from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from src.routers.ai_router import router as ai_router
from src.config import ALLOWED_ORIGINS
from src.database import engine, Base
from src.models import audit # Register models

# Init DB Tables
Base.metadata.create_all(bind=engine)

import logging
import os

# Ensure logs directory exists (in docker it should be mounted, but good practice)
os.makedirs("/app/logs", exist_ok=True)

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
    return {"message": "NSPACE Python API is running", "version": "1.0.0"}

@app.get("/health")
async def health():
    return {"status": "healthy", "service": "python-backend"}

app.include_router(ai_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)