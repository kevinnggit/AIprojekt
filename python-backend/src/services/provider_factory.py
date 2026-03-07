"""
Factory-Klasse zur Erzeugung und Verwaltung von KI-Provider-Instanzen.
Implementiert das Factory-Pattern mit einfachem Instanz-Caching (Singleton pro Provider).
"""

from .ai_provider import AIProvider, OpenAIProvider, DeepSeekProvider, MockProvider, OllamaProvider
from src.config import OPENAI_API_KEY, DEEPSEEK_API_KEY, OPENAI_MODEL


class ProviderFactory:
    """
    Zentrale Fabrik zur Erstellung von AIProvider-Objekten.
    Kapselt die Konfigurationslogik (API-Keys, Modellnamen) und gibt bei fehlendem
    API-Schlüssel automatisch auf den MockProvider zurück.
    """

    # Bereits erzeugte Instanzen werden gecacht, damit nicht bei jeder Anfrage ein neues Objekt entsteht
    _instances = {}

    @staticmethod
    def get_provider(name: str = "openai") -> AIProvider:
        """
        Gibt den Provider-Instanz für den angegebenen Namen zurück.
        Bereits erzeugte Instanzen werden aus dem Cache geladen.

        :param name: Provider-Bezeichner ("openai", "deepseek", "ollama" oder anderer -> Mock)
        :return: Konfigurierte AIProvider-Instanz
        """
        # Cache-Treffer: Instanz bereits vorhanden, direkt zurückgeben
        if name in ProviderFactory._instances:
            return ProviderFactory._instances[name]

        provider = None

        if name == "openai":
            if OPENAI_API_KEY:
                provider = OpenAIProvider(api_key=OPENAI_API_KEY, model=OPENAI_MODEL)
            else:
                # Kein API-Key konfiguriert – für Entwicklung auf Mock ausweichen
                print("OpenAI Key missing, falling back to Mock")
                provider = MockProvider()

        elif name == "deepseek":
            if DEEPSEEK_API_KEY:
                provider = DeepSeekProvider(api_key=DEEPSEEK_API_KEY)
            else:
                print("DeepSeek Key missing, falling back to Mock")
                provider = MockProvider()

        elif name == "ollama":
            # Ollama-Konfiguration erst hier laden (lazy import), da sie nicht immer benötigt wird
            from src.config import OLLAMA_BASE_URL, OLLAMA_MODEL
            provider = OllamaProvider(base_url=OLLAMA_BASE_URL, model=OLLAMA_MODEL)

        else:
            # Unbekannter Provider-Name – sicherer Fallback auf Mock
            print(f"Unknown provider {name}, using Mock")
            provider = MockProvider()

        # Instanz cachen, damit nachfolgende Aufrufe dasselbe Objekt erhalten
        ProviderFactory._instances[name] = provider
        return provider
