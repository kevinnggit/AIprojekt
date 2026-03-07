"""
Allgemeine Hilfsfunktionen für das Python-Backend.
Enthält Utility-Methoden für Antwortformatierung und Eingabevalidierung.
"""


def format_response(content: str, model: str, latency_ms: int) -> dict:
    """
    Formatiert eine KI-Antwort in das einheitliche API-Antwortformat.

    :param content: Der Antworttext des Modells
    :param model: Name des verwendeten Modells
    :param latency_ms: Antwortzeit in Millisekunden
    :return: Dictionary im standardisierten Antwortformat
    """
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
    """
    Prüft, ob eine Texteingabe gültig ist.
    Gibt False zurück, wenn der Text leer oder kein String ist.
    """
    return isinstance(text, str) and len(text.strip()) > 0
