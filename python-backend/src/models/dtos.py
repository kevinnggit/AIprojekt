from pydantic import BaseModel
from typing import List, Optional

class InferRequest(BaseModel):
    text: str
    provider: Optional[str] = "openai"

class InferResponse(BaseModel):
    model: str
    provider: str
    result: dict
    latency_ms: int

class IdeaRequest(BaseModel):
    topic: str
    count: int = 3
    provider: Optional[str] = "openai"

class IdeaResponse(BaseModel):
    topic: str
    ideas: List[str]
    model_used: str
