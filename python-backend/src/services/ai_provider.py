from abc import ABC, abstractmethod
from openai import OpenAI
import os

# 🏗️ ABSTRACT BASE CLASS (ABC)
# Definiert den Vertrag für alle KI-Provider.
class AIProvider(ABC):
    @abstractmethod
    def generate(self, prompt: str, system_prompt: str = "") -> dict:
        pass

    @abstractmethod
    def name(self) -> str:
        pass

class OpenAICompatibleProvider(AIProvider):
    def __init__(self, api_key: str, base_url: str = None, model: str = "gpt-3.5-turbo", name: str = "openai"):
        self._name = name
        self.client = OpenAI(api_key=api_key, base_url=base_url)
        self.model = model

    def name(self) -> str:
        return self._name

    def generate(self, prompt: str, system_prompt: str = "") -> dict:
        try:
            messages = []
            if system_prompt:
                messages.append({"role": "system", "content": system_prompt})
            messages.append({"role": "user", "content": prompt})

            resp = self.client.chat.completions.create(
                model=self.model,
                messages=messages,
                temperature=0.7
            )
            choice = resp.choices[0].message
            return {
                "role": choice.role,
                "content": choice.content
            }
        except Exception as e:
            return {
                "role": "system",
                "content": f"Error from {self._name}: {str(e)}"
            }

class OpenAIProvider(OpenAICompatibleProvider):
    def __init__(self, api_key: str, model: str = "gpt-3.5-turbo"):
        super().__init__(api_key=api_key, model=model, name="openai")

class DeepSeekProvider(OpenAICompatibleProvider):
    def __init__(self, api_key: str):
        super().__init__(
            api_key=api_key, 
            base_url="https://api.deepseek.com", 
            model="deepseek-chat", 
            name="deepseek"
        )

# 🦙 LOCAL AI PROVIDER (Ollama)
# Ermöglicht das Ausführen von LLMs (z.B. Llama 2) lokal auf dem Rechner.
# Vorteil: Privatsphäre & keine Kosten. Nachteil: Hardware-hungrig.
class OllamaProvider(OpenAICompatibleProvider):
    def __init__(self, base_url: str, model: str):
        super().__init__(
            api_key="ollama", # Key is required by client but ignored by Ollama Server
            base_url=base_url, # e.g., http://localhost:11434/v1
            model=model,
            name="ollama"
        )

class MockProvider(AIProvider):
    def name(self) -> str:
        return "mock"

    def generate(self, prompt: str, system_prompt: str = "") -> dict:
        return {
            "role": "assistant",
            "content": f"[MOCK] Processed by MockProvider: {prompt} (System: {system_prompt})"
        }
