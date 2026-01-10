from fastapi.testclient import TestClient
from src.main import app
from src.services.ai_service import ai_service
import pytest

client = TestClient(app)

def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"

def test_generate_ideas_mock():
    # Force mock mode
    original_client = ai_service.client
    ai_service.client = None
    
    try:
        response = client.post(
            "/api/ki/generate-ideas",
            json={"topic": "Integration Test", "count": 2}
        )
        assert response.status_code == 200
        data = response.json()
        assert data["topic"] == "Integration Test"
        assert len(data["ideas"]) == 2
        assert data["model_used"] == "mock-model"
        assert "(Mock)" in data["ideas"][0]
    finally:
        # Restore client
        ai_service.client = original_client

def test_info_endpoint():
    response = client.get("/api/ki/info")
    assert response.status_code == 200
    data = response.json()
    assert "ready" in data
