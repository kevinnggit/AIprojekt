import os
from dotenv import load_dotenv

# Lädt .env aus dem Projektroot
load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(__file__)), '..', '.env'))

MODEL_NAME = os.getenv("MODEL_NAME", "mock-model")
DEVICE = os.getenv("DEVICE", "cpu")
ALLOWED_ORIGINS = os.getenv("ALLOWED_ORIGINS", "http://localhost:3000,http://vue-frontend").split(",")

# OpenAI - liest aus Umgebungsvariablen (Docker) oder .env (lokal)
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY")
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-3.5-turbo")
OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://host.docker.internal:11434/v1")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "llama2")

SYSTEM_PROMPT = os.getenv("SYSTEM_PROMPT", "You are a helpful AI assistant for KI demos.")

# Database
DB_HOST = os.getenv("DB_HOST", "postgres")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_USER = os.getenv("DB_USER", "nspace")
DB_PASSWORD = os.getenv("DB_PASSWORD", "securepassword")
DB_NAME = os.getenv("DB_NAME", "nspacedb")

DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"