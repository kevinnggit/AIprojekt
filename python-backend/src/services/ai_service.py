from openai import OpenAI
from src.config import OPENAI_API_KEY, OPENAI_MODEL, SYSTEM_PROMPT
from src.models.dtos import IdeaResponse
import time

class AIService:
    def __init__(self):
        self.api_key = OPENAI_API_KEY
        self.client = None
        if self.api_key:
            self.client = OpenAI(api_key=self.api_key)
        else:
            print("WARNING: No OpenAI API Key found. Using Mock mode.")

    def mock_inference(self, text: str) -> str:
        # Simple mock response
        return f"[MOCK] Processed: {text}"

    def mock_ideas(self, topic: str, count: int) -> list[str]:
        return [f"Idea {i+1} for {topic} (Mock)" for i in range(count)]

    def infer_text(self, text: str) -> dict:
        t0 = time.perf_counter()
        
        # Fallback-Logik: Wenn kein API-Key da ist, nutze Mock (kostet nichts).
        if not self.client:
            result_text = self.mock_inference(text)
            role = "assistant"
        else:
            try:
                # Echter Call an OpenAI
                resp = self.client.chat.completions.create(
                    model=OPENAI_MODEL,
                    messages=[
                        {"role": "system", "content": SYSTEM_PROMPT},
                        {"role": "user", "content": text}
                    ],
                    temperature=0.7,
                )
                choice = resp.choices[0].message
                result_text = choice.content
                role = choice.role
            except Exception as e:
                # Fallback on error
                print(f"OpenAI Error: {e}")
                result_text = f"Error: {str(e)}"
                role = "system"

        latency_ms = int((time.perf_counter() - t0) * 1000)
        
        return {
            "model": OPENAI_MODEL if self.client else "mock-model",
            "provider": "openai" if self.client else "mock",
            "result": {
                "role": role,
                "content": result_text
            },
            "latency_ms": latency_ms
        }

    def generate_project_ideas(self, topic: str, count: int) -> IdeaResponse:
        prompt = f"Generate {count} creative project ideas for the topic: '{topic}'. Return just a list of ideas."
        
        if not self.client:
            ideas = self.mock_ideas(topic, count)
        else:
            try:
                inference = self.infer_text(prompt)
                content = inference["result"]["content"]
                # Naive parsing for now - split by newlines and clean up
                lines = content.split('\n')
                ideas = [line.strip('- ').strip() for line in lines if line.strip()]
                # Limit to requested count if possible
                ideas = ideas[:count]
            except Exception:
                ideas = ["Error generating ideas"]

        return IdeaResponse(
            topic=topic,
            ideas=ideas,
            model_used=OPENAI_MODEL if self.client else "mock-model"
        )

ai_service = AIService()
