from .ai_provider import AIProvider, OpenAIProvider, DeepSeekProvider, MockProvider, OllamaProvider
from src.config import OPENAI_API_KEY, DEEPSEEK_API_KEY, OPENAI_MODEL

class ProviderFactory:
    _instances = {} # Cache instances (Singleton-like per provider)

    # 🏭 FACTORY PATTERN
    # Centralizes object creation. "Separation of Concerns":
    # The business logic doesn't need to know about API keys or class names.
    @staticmethod
    def get_provider(name: str = "openai") -> AIProvider:
        if name in ProviderFactory._instances:
            return ProviderFactory._instances[name]
        
        provider = None
        if name == "openai":
            if OPENAI_API_KEY:
                provider = OpenAIProvider(api_key=OPENAI_API_KEY, model=OPENAI_MODEL)
            else:
                print("OpenAI Key missing, falling back to Mock")
                provider = MockProvider()
        
        elif name == "deepseek":
            if DEEPSEEK_API_KEY:
                provider = DeepSeekProvider(api_key=DEEPSEEK_API_KEY)
            else:
                print("DeepSeek Key missing, falling back to Mock")
                provider = MockProvider()

        elif name == "ollama":
            from src.config import OLLAMA_BASE_URL, OLLAMA_MODEL
            provider = OllamaProvider(base_url=OLLAMA_BASE_URL, model=OLLAMA_MODEL)
        
        else:
            # Default to Mock for unknown
            print(f"Unknown provider {name}, using Mock")
            provider = MockProvider()

        ProviderFactory._instances[name] = provider
        return provider
