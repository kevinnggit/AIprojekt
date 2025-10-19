# Hilfsfunktionen

def format_response(content: str, model: str, latency_ms: int) -> dict:
    """Formatiert eine API-Antwort."""
    return {
        "model": model,
        "provider": "openai",
        "result": {
            "role": "assistant",
            "content": content
        },
        "latency_ms": latency_ms
    }

def validate_text_input(text: str) -> bool:
    """Validiert Texteingaben."""
    return isinstance(text, str) and len(text.strip()) > 0