from time import perf_counter
from typing import Any, Dict
from fastapi import APIRouter, HTTPException
from openai import OpenAI
from .config import OPENAI_API_KEY, OPENAI_MODEL, SYSTEM_PROMPT

router = APIRouter(prefix="/api/ki", tags=["KI"])

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

@router.get("/info")
def info():
    try:
        get_client()
        return {"model": "openai", "provider": "openai", "ready": True}
    except Exception:
        return {"model": "openai", "provider": "openai", "ready": False}

@router.post("/infer")
def infer(req: dict):
    try:
        return infer_text(req["text"])
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))