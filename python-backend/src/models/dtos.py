"""
Pydantic-Datenmodelle (DTOs) für die KI-API-Endpunkte.
Definiert die Struktur von Anfrage- und Antwortobjekten für Inferenz und Ideengenerierung.
"""

from pydantic import BaseModel
from typing import List, Optional


class InferRequest(BaseModel):
    """Anfrageobjekt für den /infer-Endpunkt. Enthält den Eingabetext und den gewünschten Provider."""

    text: str
    provider: Optional[str] = "openai"  # Standard: OpenAI; alternativ "deepseek", "ollama", "mock"


class InferResponse(BaseModel):
    """Antwortobjekt für den /infer-Endpunkt mit Modellinformationen und der KI-Antwort."""

    model: str       # Verwendetes Modell, z.B. "gpt-3.5-turbo"
    provider: str    # Name des Providers, z.B. "openai"
    result: dict     # Enthält "role" und "content" der KI-Antwort
    latency_ms: int  # Gemessene Antwortzeit in Millisekunden


class IdeaRequest(BaseModel):
    """Anfrageobjekt für die Projektideen-Generierung. Gibt Thema, Anzahl und Provider vor."""

    topic: str
    count: int = 3               # Standardmäßig drei Ideen generieren
    provider: Optional[str] = "openai"


class IdeaResponse(BaseModel):
    """Antwortobjekt für die Ideengenerierung mit dem Thema, einer Ideenliste und dem Modellnamen."""

    topic: str
    ideas: List[str]   # Liste der generierten Projektideen
    model_used: str    # Modell, das zur Generierung verwendet wurde
