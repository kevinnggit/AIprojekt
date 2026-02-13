# 03. Python & AI Service: Das Gehirn (Sprint 3)

Unser Python-Backend ist spezialisiert. Es tut genau eine Sache: **Künstliche Intelligenz**.
Wir nutzen **FastAPI**, ein modernes High-Performance Framework, das perfekt für AI/ML-Workloads geeignet ist.

## 1. Warum Python UND Java? (Microservices)
Man könnte fragen: "Warum macht Java das nicht auch?"
-   **Java:** Stark in Enterprise-Daten, Security, Struktur.
-   **Python:** Die #1 Sprache für AI. Alle wichtigen Libraries (PyTorch, OpenAI, LangChain) sind hier zuhause.

Wir nutzen den **Best-of-Breed Ansatz**: Jede Sprache für das, was sie am besten kann.

## 2. FastAPI vs. Spring Boot
Vergleich der Konzepte:

| Konzept | Java (Spring Boot) | Python (FastAPI) |
| :--- | :--- | :--- |
| **Request Handling** | `@RestController` | `@app.get(...)` / `APIRouter` |
| **Dependency Injection** | `@Autowired` / Constructor | Native Parameter (`Depending`) oder explizit (wie bei uns) |
| **Daten-Modelle** | POJOs / DTOs | **Pydantic Models** |
| **Async** | Threads / Reactive Stack | Native `async` / `await` |

## 3. Der Code-Flow (Deep Dive)

Wenn das Frontend eine AI-Anfrage stellt:

1.  **Router (`ai_router.py`):**
    Nimmt den Request an. Validiert die JSON-Daten *automatisch* mittels Pydantic (`InferRequest`).
    
    ```python
    @router.post("/infer", response_model=InferResponse)
    def infer(req: InferRequest):
        # ... ruft Service auf
    ```

2.  **Service (`ai_service.py`):**
    Entscheidet: "Habe ich einen OpenAI Key?".
    -   **Ja:** Ruft OpenAI API auf.
    -   **Nein:** Nutzt die `mock_inference` Methode (Fallback für Entwicklung ohne Kosten).

    Dieses Pattern nennt man **Facade** oder **Strategy Pattern** (wenn auch vereinfacht). Der Router weiß nicht, ob eine echte AI antwortet. Er will nur Text.

## 4. Pydantic: Das Killer-Feature
Schauen Sie in `models/dtos.py`.
Pydantic garantiert, dass die Daten valide sind. Wenn `count` ein String ist, wirft FastAPI einen Fehler ans Frontend, *bevor* unser Code überhaupt läuft. Das spart uns hunderte Zeilen `if request.count is not int...`.
