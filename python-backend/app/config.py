import os
from dotenv import load_dotenv

# Lädt .env aus dem Projektroot (nur wenn lokal)
load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(__file__)), '..', '.env'))

MODEL_NAME = os.getenv("MODEL_NAME", "mock-model")
DEVICE = os.getenv("DEVICE", "cpu")
ALLOWED_ORIGINS = os.getenv("ALLOWED_ORIGINS", "http://localhost:3000,http://vue-frontend").split(",")

# OpenAI - liest aus Umgebungsvariablen (Docker) oder .env (lokal)
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
SYSTEM_PROMPT = os.getenv("SYSTEM_PROMPT", "You are a helpful AI assistant for KI demos.")