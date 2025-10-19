# Prompt-Vorlagen für verschiedene Anwendungsfälle

SYSTEM_PROMPTS = {
    "assistant": "You are a helpful AI assistant for KI demos.",
    "coder": "You are an expert Python developer. Provide clean, efficient code solutions.",
    "translator": "You are a professional translator. Translate accurately between languages.",
    "summarizer": "You are a text summarization expert. Provide concise, accurate summaries."
}

def get_prompt(prompt_type: str = "assistant") -> str:
    """Holt eine Prompt-Vorlage basierend auf dem Typ."""
    return SYSTEM_PROMPTS.get(prompt_type, SYSTEM_PROMPTS["assistant"])