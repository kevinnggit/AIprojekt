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