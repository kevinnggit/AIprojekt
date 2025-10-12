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