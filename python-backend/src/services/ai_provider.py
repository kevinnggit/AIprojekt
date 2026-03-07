"""
Abstrakte Basisklasse und konkrete Implementierungen aller unterstützten KI-Provider.
Unterstützt OpenAI, DeepSeek, Ollama (lokal) und einen Mock-Provider für Tests.
"""

from abc import ABC, abstractmethod
from openai import OpenAI
import os


class AIProvider(ABC):
    """
    Abstrakte Basisklasse, die den Vertrag für alle KI-Provider definiert.
    Jeder Provider muss generate() und name() implementieren.
    """

    @abstractmethod
    def generate(self, prompt: str, system_prompt: str = "") -> dict:
        """Sendet einen Prompt an den Provider und gibt die Antwort als Dictionary zurück."""
        pass

    @abstractmethod
    def name(self) -> str:
        """Gibt den eindeutigen Bezeichner des Providers zurück (z.B. 'openai')."""
        pass


class OpenAICompatibleProvider(AIProvider):
    """
    Generische Implementierung für alle Provider, die die OpenAI-API-Schnittstelle nutzen.
    Dient als Basisklasse für OpenAI, DeepSeek und Ollama, da alle dieselbe Client-Bibliothek verwenden.
    """

    def __init__(self, api_key: str, base_url: str = None, model: str = "gpt-3.5-turbo", name: str = "openai"):
        """
        Initialisiert den Provider mit Verbindungsparametern.

        :param api_key: API-Schlüssel für den Dienst
        :param base_url: Optionale Basis-URL (abweichend vom OpenAI-Standard, z.B. für Ollama)
        :param model: Zu verwendendes Modell
        :param name: Interner Bezeichner des Providers
        """
        self._name = name
        # OpenAI-Client ist kompatibel mit anderen Diensten, solange sie dasselbe API-Schema nutzen
        self.client = OpenAI(api_key=api_key, base_url=base_url)
        self.model = model

    def name(self) -> str:
        """Gibt den Provider-Bezeichner zurück."""
        return self._name

    def generate(self, prompt: str, system_prompt: str = "") -> dict:
        """
        Sendet eine Chat-Completion-Anfrage an den konfigurierten Provider.
        Gibt bei Fehlern eine strukturierte Fehlermeldung zurück, statt eine Ausnahme zu werfen.

        :param prompt: Der Benutzer-Prompt
        :param system_prompt: Optionaler Systemkontext, der dem Gespräch vorangestellt wird
        :return: Dictionary mit "role" und "content" der Antwort
        """
        try:
            messages = []
            # Systemprompt nur hinzufügen, wenn einer geliefert wurde
            if system_prompt:
                messages.append({"role": "system", "content": system_prompt})
            messages.append({"role": "user", "content": prompt})

            resp = self.client.chat.completions.create(
                model=self.model,
                messages=messages,
                temperature=0.7  # Kreativitätsstufe; 0 = deterministisch, 1 = sehr kreativ
            )
            choice = resp.choices[0].message
            return {
                "role": choice.role,
                "content": choice.content
            }
        except Exception as e:
            # Fehler werden als Antwort zurückgegeben, damit der Service weiterläuft
            return {
                "role": "system",
                "content": f"Error from {self._name}: {str(e)}"
            }


class OpenAIProvider(OpenAICompatibleProvider):
    """Konkreter Provider für die offizielle OpenAI-API (ChatGPT)."""

    def __init__(self, api_key: str, model: str = "gpt-3.5-turbo"):
        super().__init__(api_key=api_key, model=model, name="openai")


class DeepSeekProvider(OpenAICompatibleProvider):
    """
    Provider für die DeepSeek-API.
    Nutzt dieselbe Schnittstelle wie OpenAI, leitet Anfragen jedoch an die DeepSeek-Server weiter.
    """

    def __init__(self, api_key: str):
        super().__init__(
            api_key=api_key,
            base_url="https://api.deepseek.com",
            model="deepseek-chat",
            name="deepseek"
        )


class OllamaProvider(OpenAICompatibleProvider):
    """
    Provider für lokal laufende LLMs über Ollama (z.B. Llama 2, Mistral).
    Vorteil: Volle Datenkontrolle, keine laufenden API-Kosten.
    Nachteil: Benötigt leistungsfähige lokale Hardware.
    """

    def __init__(self, base_url: str, model: str):
        """
        :param base_url: URL des lokalen Ollama-Servers, z.B. http://localhost:11434/v1
        :param model: Name des geladenen Ollama-Modells, z.B. "llama2"
        """
        super().__init__(
            api_key="ollama",  # Pflichtfeld des Clients, wird vom Ollama-Server ignoriert
            base_url=base_url,
            model=model,
            name="ollama"
        )


class MockProvider(AIProvider):
    """
    Dummy-Provider für lokale Entwicklung und Tests ohne echte API-Anbindung.
    Gibt eine statische, vorhersagbare Antwort zurück.
    """

    def name(self) -> str:
        return "mock"

    def generate(self, prompt: str, system_prompt: str = "") -> dict:
        """Gibt eine feste Mock-Antwort zurück, die Prompt und Systemprompt spiegelt."""
        return {
            "role": "assistant",
            "content": f"[MOCK] Processed by MockProvider: {prompt} (System: {system_prompt})"
        }
