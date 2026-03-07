"""
Zentrale Konfigurationsdatei des Backends.
Liest alle relevanten Einstellungen aus Umgebungsvariablen oder einer .env-Datei.
"""

import os
from dotenv import load_dotenv

# Lädt .env aus dem Projektroot (zwei Ebenen über diesem Modul)
load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(__file__)), '..', '.env'))

# --- Allgemeine Einstellungen ---
MODEL_NAME = os.getenv("MODEL_NAME", "mock-model")
DEVICE = os.getenv("DEVICE", "cpu")
# Erlaubte Ursprünge für CORS – kommaseparierte Liste wird in ein Python-Array umgewandelt
ALLOWED_ORIGINS = os.getenv("ALLOWED_ORIGINS", "http://localhost:3000,http://vue-frontend").split(",")

# --- KI-Provider-Konfiguration ---
# API-Schlüssel werden ausschließlich aus Umgebungsvariablen gelesen (nie hartkodieren!)
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY")
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-3.5-turbo")

# Ollama läuft standardmäßig auf dem Docker-Host; host.docker.internal ermöglicht
# den Zugriff vom Container auf den lokalen Rechner
OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://host.docker.internal:11434/v1")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "llama2")

# Standard-Systemprompt, der an jeden KI-Aufruf vorangestellt wird
SYSTEM_PROMPT = os.getenv("SYSTEM_PROMPT", "You are a helpful AI assistant for KI demos.")

# --- Datenbankverbindung (PostgreSQL) ---
DB_HOST = os.getenv("DB_HOST", "postgres")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_USER = os.getenv("DB_USER", "nspace")
DB_PASSWORD = os.getenv("DB_PASSWORD", "securepassword")
DB_NAME = os.getenv("DB_NAME", "nspacedb")

# Vollständige Connection-URL für SQLAlchemy aus den einzelnen Parametern zusammengesetzt
DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
