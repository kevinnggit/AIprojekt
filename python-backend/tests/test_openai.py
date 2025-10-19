import pytest
from src.openai_client import get_client, infer_text

def test_get_client_without_key():
    """Testet dass ohne API-Key ein Fehler geworfen wird."""
    with pytest.raises(RuntimeError, match="OPENAI_API_KEY not set"):
        get_client()

def test_infer_text_mock():
    """Testet die infer_text Funktion (Mock)."""
    # Hier könnten Mock-Tests stehen
    pass