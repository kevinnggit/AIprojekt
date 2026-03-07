"""
FastAPI-Router für alle KI-Endpunkte des Backends.
Gruppiert die Routen unter dem Präfix /api/ki und delegiert die Verarbeitung an den AIService.
"""

from fastapi import APIRouter, HTTPException
from src.services.ai_service import ai_service
from src.models.dtos import InferRequest, InferResponse, IdeaRequest, IdeaResponse

# APIRouter gruppiert zusammengehörige Endpunkte; das Präfix wird jedem Pfad vorangestellt
router = APIRouter(prefix="/api/ki", tags=["KI"])


@router.get("/info")
def info():
    """
    Gibt den aktuellen Status des KI-Dienstes zurück.
    Zeigt an, ob ein echter Provider (OpenAI) verfügbar ist oder der Mock-Modus aktiv ist.
    """
    ready = ai_service.client is not None
    return {
        "model": "openai" if ready else "mock",
        "provider": "openai" if ready else "mock",
        "ready": ready
    }


# Pydantic Magic: "req: InferRequest" validiert den Request-Body automatisch.
# Stimmt die JSON-Struktur nicht mit dem Modell überein, antwortet FastAPI mit HTTP 422.
@router.post("/infer", response_model=InferResponse)
def infer(req: InferRequest):
    """
    Führt eine Textinferenz mit dem angegebenen Provider durch und gibt das Ergebnis zurück.
    Bei einem internen Fehler wird HTTP 500 mit der Fehlermeldung zurückgegeben.
    """
    try:
        return ai_service.infer_text(req.text, provider_name=req.provider or "openai")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/generate-ideas", response_model=IdeaResponse)
def generate_ideas(req: IdeaRequest):
    """
    Generiert eine Liste von Projektideen zum angegebenen Thema mithilfe des gewählten Providers.
    Bei einem Fehler wird HTTP 500 zurückgegeben.
    """
    try:
        return ai_service.generate_project_ideas(req.topic, req.count, provider_name=req.provider or "openai")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
