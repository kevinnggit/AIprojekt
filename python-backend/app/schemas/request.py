from pydantic import BaseModel, Field

class InferRequest(BaseModel):
    text: str = Field(..., min_length=1, description="Eingabetext für das LLM")