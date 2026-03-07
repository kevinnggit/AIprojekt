"""
Zentraler KI-Service, der Anfragen an den passenden Provider weiterleitet.
Orchestriert Provider-Auswahl, Inferenz und Audit-Logging für alle KI-Operationen.
"""

from src.services.provider_factory import ProviderFactory
from src.services.audit_service import AuditService
from src.config import SYSTEM_PROMPT
from src.models.dtos import IdeaResponse
import time


class AIService:
    """
    Hauptservice für KI-Anfragen.
    Kapselt die Logik für Textinferenz und Ideengenerierung und protokolliert alle Aufrufe per Audit-Log.
    """

    def infer_text(self, text: str, provider_name: str = "openai") -> dict:
        """
        Führt eine Textinferenz mit dem angegebenen Provider durch.
        Misst die Latenz und schreibt nach jedem Aufruf einen Audit-Log-Eintrag.

        :param text: Der Eingabetext (User-Prompt)
        :param provider_name: Name des zu verwendenden Providers (z.B. "openai", "mock")
        :return: Dictionary mit Modellinformationen, Provider-Name, Ergebnis und Latenz
        """
        # Startzeit für Latenz-Messung festhalten
        t0 = time.perf_counter()

        provider = ProviderFactory.get_provider(provider_name)
        result = provider.generate(text, system_prompt=SYSTEM_PROMPT)

        # Latenz in Millisekunden berechnen
        latency_ms = int((time.perf_counter() - t0) * 1000)

        # Jede Anfrage wird für Nachvollziehbarkeit und Kostenkontrolle geloggt
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
        """
        Generiert eine Liste von Projektideen zu einem gegebenen Thema.
        Verwendet intern infer_text und parst die Antwort zeilenweise.

        :param topic: Das Thema, zu dem Ideen generiert werden sollen
        :param count: Gewünschte Anzahl an Ideen
        :param provider_name: Name des zu verwendenden Providers
        :return: IdeaResponse-Objekt mit Thema, Ideen und verwendetem Modell
        """
        prompt = f"Generate {count} creative project ideas for the topic: '{topic}'. Return just a list of ideas."

        try:
            inference = self.infer_text(prompt, provider_name=provider_name)
            content = inference["result"]["content"]

            # Antworttext zeilenweise aufteilen und Listenzeichen entfernen
            lines = content.split('\n')
            ideas = [line.strip('- ').strip() for line in lines if line.strip()]

            # Auf die gewünschte Anzahl begrenzen, falls die KI mehr geliefert hat
            ideas = ideas[:count]
        except Exception as e:
            ideas = [f"Error generating ideas: {str(e)}"]

        return IdeaResponse(
            topic=topic,
            ideas=ideas,
            # Bei Ausnahme oben ist 'inference' nicht definiert – daher Fallback auf "error"
            model_used=inference["model"] if 'inference' in locals() else "error"
        )


# Singleton-Instanz des Services – wird von den Routern direkt importiert
ai_service = AIService()
