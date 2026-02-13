from fastapi import APIRouter, HTTPException
from src.services.ai_service import ai_service
from src.models.dtos import InferRequest, InferResponse, IdeaRequest, IdeaResponse

# APIRouter gruppiert Pfade. Prefix "/api/ki" bedeutet: Alles hier fängt damit an.
router = APIRouter(prefix="/api/ki", tags=["KI"])

@router.get("/info")
def info():
    ready = ai_service.client is not None
    return {
        "model": "openai" if ready else "mock",
        "provider": "openai" if ready else "mock",
        "ready": ready
    }

# Pydantic Magic: "req: InferRequest" validiert den Input automatisch.
# Wenn das JSON im Body nicht zum Modell passt, wirft FastAPI einen 422 Error.
@router.post("/infer", response_model=InferResponse)
def infer(req: InferRequest):
    try:
        return ai_service.infer_text(req.text, provider_name=req.provider or "openai")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/generate-ideas", response_model=IdeaResponse)
def generate_ideas(req: IdeaRequest):
    try:
        return ai_service.generate_project_ideas(req.topic, req.count, provider_name=req.provider or "openai")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
