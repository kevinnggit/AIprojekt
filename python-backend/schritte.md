# Schritte: KI-API mit OpenAI im Python-Backend

Ziel: Erste funktionsfähige KI-API ohne eigenes Modell (OpenAI API-Key), klare Struktur, .env-Konfiguration, saubere Endpoints.

## Schritt 1: Abhängigkeiten

Datei `python-backend/requirements.txt` ergänzen:

```text
openai>=1.44.0
```

Installation lokal:

```bash
cd python-backend
pip install -r requirements.txt
```

## Schritt 2: .env im Projekt-Root

Datei `.env` (im Projektwurzelverzeichnis) anlegen/ergänzen:

```bash
OPENAI_API_KEY=sk-...dein_key...
OPENAI_MODEL=gpt-4o-mini
ALLOWED_ORIGINS=http://localhost:3000,http://vue-frontend
SYSTEM_PROMPT=You are a helpful AI assistant for KI demos.
```

## Schritt 3: Konfiguration laden

Datei `python-backend/app/config.py` (falls nötig ersetzen):

```python
import os
from dotenv import load_dotenv

# Lädt .env aus dem Projektroot
load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(__file__)), '..', '.env'))

MODEL_NAME = os.getenv("MODEL_NAME", "mock-model")
DEVICE = os.getenv("DEVICE", "cpu")
ALLOWED_ORIGINS = os.getenv("ALLOWED_ORIGINS", "http://localhost:3000,http://vue-frontend").split(",")

# OpenAI
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
SYSTEM_PROMPT = os.getenv("SYSTEM_PROMPT", "You are a helpful AI assistant for KI demos.")
```

## Schritt 4: OpenAI-Client & Inferenz

Datei `python-backend/app/models/loader.py` (Inhalt):

```python
from time import perf_counter
from typing import Any, Dict

from openai import OpenAI
from ..config import OPENAI_API_KEY, OPENAI_MODEL, SYSTEM_PROMPT

_client: OpenAI | None = None

def get_client() -> OpenAI:
    global _client
    if _client is None:
        if not OPENAI_API_KEY:
            raise RuntimeError("OPENAI_API_KEY not set")
        _client = OpenAI(api_key=OPENAI_API_KEY)
    return _client


def infer_text(text: str) -> Dict[str, Any]:
    client = get_client()
    t0 = perf_counter()
    resp = client.chat.completions.create(
        model=OPENAI_MODEL,
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": text}
        ],
        temperature=0.2,
    )
    latency_ms = int((perf_counter() - t0) * 1000)
    choice = resp.choices[0].message
    return {
        "model": OPENAI_MODEL,
        "provider": "openai",
        "result": {
            "role": choice.role,
            "content": choice.content
        },
        "latency_ms": latency_ms
    }
```

## Schritt 5: Schemas

Datei `python-backend/app/schemas/request.py`:

```python
from pydantic import BaseModel, Field

class InferRequest(BaseModel):
    text: str = Field(..., min_length=1, description="Eingabetext für das LLM")
```

Datei `python-backend/app/schemas/response.py`:

```python
from pydantic import BaseModel
from typing import Any, Dict

class InferResponse(BaseModel):
    model: str
    provider: str
    result: Dict[str, Any]
    latency_ms: int

class InfoResponse(BaseModel):
    model: str
    provider: str
    ready: bool
```

## Schritt 6: Router & Endpoints

Datei `python-backend/app/routers/ai.py`:

```python
from fastapi import APIRouter, HTTPException
from ..models.loader import infer_text, get_client
from ..schemas.request import InferRequest
from ..schemas.response import InferResponse, InfoResponse

router = APIRouter(prefix="/api/ki", tags=["KI"])

@router.get("/info", response_model=InfoResponse)
def info():
    try:
        get_client()
        return {"model": "openai", "provider": "openai", "ready": True}
    except Exception:
        return {"model": "openai", "provider": "openai", "ready": False}

@router.post("/infer", response_model=InferResponse)
def infer(req: InferRequest):
    try:
        return infer_text(req.text)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
```

## Schritt 7: main.py

Datei `python-backend/main.py` (Router einbinden & CORS nutzen):

```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers.ai import router as ai_router
from app.config import ALLOWED_ORIGINS

app = FastAPI(
    title="NSPACE Python API",
    description="KI Python Backend for NSPACE Portfolio",
    version="1.0.0"
)

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
```

## Schritt 8: Testen (lokal)

```bash
cd python-backend
pip install -r requirements.txt
uvicorn main:app --reload
# Swagger: http://localhost:8000/docs
```

Test-Request (Beispiel):

- POST `http://localhost:8000/api/ki/infer`
- Body:

```json
{ "text": "Schreibe einen Satz auf Deutsch über den Weltraum." }
```

Erwartet: JSON mit `provider=openai`, `model`, `result.content` (Textantwort), `latency_ms`.

## Schritt 9: Optional – Frontend

- In `vue-frontend/src/views/KiPython.vue` Formular bauen, das `POST /api/ki/infer` nutzt.
- Basis-URL aus `import.meta.env.VITE_PYTHON_API_URL`.

## Schritt 10: Docker (optional)

Wenn lokal alles funktioniert:

```bash
docker compose up -d --build
```

Hinweise:
- `OPENAI_API_KEY` muss in `.env` gesetzt sein (nicht ins Image kopieren).
- Für Produktion: Key via Compose-Env/Secrets injizieren.
