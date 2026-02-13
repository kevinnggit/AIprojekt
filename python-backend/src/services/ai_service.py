from src.services.provider_factory import ProviderFactory
from src.services.audit_service import AuditService
from src.config import SYSTEM_PROMPT
from src.models.dtos import IdeaResponse
import time

class AIService:
    def infer_text(self, text: str, provider_name: str = "openai") -> dict:
        t0 = time.perf_counter()
        
        provider = ProviderFactory.get_provider(provider_name)
        result = provider.generate(text, system_prompt=SYSTEM_PROMPT)
        
        latency_ms = int((time.perf_counter() - t0) * 1000)
        
        # AUDIT LOGGING
        AuditService.log_request(
            provider=provider.name(),
            model=getattr(provider, 'model', 'unknown'),
            prompt=text,
            response=result.get("content", ""),
            latency_ms=latency_ms
        )
        
        return {
            "model": getattr(provider, "model", "unknown-model"),
            "provider": provider.name(),
            "result": result,
            "latency_ms": latency_ms
        }

    def generate_project_ideas(self, topic: str, count: int, provider_name: str = "openai") -> IdeaResponse:
        prompt = f"Generate {count} creative project ideas for the topic: '{topic}'. Return just a list of ideas."
        
        try:
            inference = self.infer_text(prompt, provider_name=provider_name)
            content = inference["result"]["content"]
            # Naive parsing for now - split by newlines and clean up
            lines = content.split('\n')
            ideas = [line.strip('- ').strip() for line in lines if line.strip()]
            # Limit to requested count if possible
            ideas = ideas[:count]
        except Exception as e:
            ideas = [f"Error generating ideas: {str(e)}"]

        return IdeaResponse(
            topic=topic,
            ideas=ideas,
            model_used=inference["model"] if 'inference' in locals() else "error"
        )

ai_service = AIService()
