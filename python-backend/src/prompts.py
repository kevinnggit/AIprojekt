"""
Vordefinierte Systemprompt-Vorlagen für verschiedene KI-Anwendungsfälle.
Die gewünschte Vorlage wird über einen Typbezeichner abgerufen.
"""

# Verfügbare Systemprompts, geordnet nach Anwendungsfall
SYSTEM_PROMPTS = {
    "assistant": "You are a helpful AI assistant for KI demos.",
    "coder": "You are an expert Python developer. Provide clean, efficient code solutions.",
    "translator": "You are a professional translator. Translate accurately between languages.",
    "summarizer": "You are a text summarization expert. Provide concise, accurate summaries."
}


def get_prompt(prompt_type: str = "assistant") -> str:
    """
    Gibt den Systemprompt für den angegebenen Typ zurück.
    Wird kein passender Typ gefunden, fällt die Funktion auf den Standard-Assistenten zurück.
    """
    return SYSTEM_PROMPTS.get(prompt_type, SYSTEM_PROMPTS["assistant"])
