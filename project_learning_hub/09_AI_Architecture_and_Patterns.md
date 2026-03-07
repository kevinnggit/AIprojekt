# 09. AI Architecture & Patterns (Sprint 9)

Willkommen zur Revolution.
Wir haben unser System von einem "One-Trick-Pony" (nur OpenAI) zu einem flexiblen **Multi-Provider System** mit vollständiger Transparenz umgebaut.
Das Zauberwort heißt: **Decoupling** — und **Audit-Logging**.

## 1. The Factory Pattern (`ProviderFactory`)
Stell dir vor, du hast eine Steckdose. Es ist dir egal, ob der Strom aus Windkraft oder Kohle kommt.
Genauso ist es unserem `AIService` egal, welche KI antwortet.

### Das Problem
Ohne Factory sähe unser Code so aus:
```python
if provider == "openai":
    client = OpenAI(...)
elif provider == "deepseek":
    client = DeepSeek(...)
# Spaghetti-Code bei jedem neuen Provider!
```

### Die Lösung: Factory
Eine zentrale Fabrik (`ProviderFactory`), die entscheidet, was erstellt wird.
```python
# Der Service sagt nur: "Gib mir 'deepseek'!"
provider = ProviderFactory.get_provider("deepseek")
```
Die Factory kümmert sich um API-Keys, Instanziierung und Caching (Singleton-ähnlich).
Fehlt ein API-Key, liefert sie automatisch den `MockProvider` zurück — der Rest des Systems merkt es nicht.

## 2. Polymorphism & Abstract Base Classes (ABC)
Damit die Factory funktioniert, müssen alle Provider **gleich aussehen**.
Sie müssen denselben "Vertrag" erfüllen.

In Python nutzen wir `ABC` (Abstract Base Classes):
```python
class AIProvider(ABC):
    @abstractmethod
    def generate(self, prompt: str) -> str:
        pass
```
Jeder Provider (`OpenAIProvider`, `DeepSeekProvider`, `OllamaProvider`, `MockProvider`) **MUSS** diese `generate`-Methode implementieren.
Das nennt man **Polymorphismus**: Dem Service ist es egal, *welches* Objekt er hat, solange es `generate()` kann.

### DeepSeek & OpenAI: Vererbung in der Praxis
Da DeepSeek eine zu OpenAI kompatible API anbietet, nutzen wir Vererbung statt doppelten Code:
`DeepSeekProvider` erbt von `OpenAICompatibleProvider`. Wir ändern nur die `base_url` auf `https://api.deepseek.com`.

Das ist **Code Reuse at its finest** — eine Änderung an der Basis-Implementierung gilt automatisch für beide Provider.

## 3. Frontend-Backend Handshake
Wie weiß der Server, was der User will?

1.  **Frontend (`KiPython.vue`):** Dropdown ändert den reaktiven State `provider`.
2.  **API Call:** `fetch(..., body: { provider: 'deepseek', prompt: '...' })`
3.  **Router (`ai_router.py`):** Liest `req.provider` und `req.prompt`.
4.  **Service:** `factory.get_provider(req.provider).generate(req.prompt)`

Ein durchgehender, sauberer Datenfluss von der UI bis auf Architektur-Ebene.

## 4. Audit Logging: Wer nutzt die KI — und wie?

Ein KI-System ohne Logging ist eine Black Box. Wir protokollieren daher **jeden** Request automatisch in der Datenbank.

### Die Middleware-Strategie
Statt überall manuell zu loggen, fangen wir die KI-Anfrage zentral im `AIService` ab:

```python
def generate_response(prompt: str, provider_name: str, db: Session):
    start = perf_counter()               # 1. Startzeit messen
    provider = ProviderFactory.get_provider(provider_name)
    response = provider.generate(prompt) # 2. KI antworten lassen
    latency = int((perf_counter() - start) * 1000)  # 3. Latenz berechnen

    audit_service.log_request(           # 4. Alles in die DB schreiben
        db, provider_name, prompt, response, latency
    )
```

### Was wir speichern (`ai_audit_logs`)
- Welcher **Provider** geantwortet hat
- Eine **Vorschau** von Prompt und Antwort (erste 500 Zeichen)
- Die **Latenz in Millisekunden** — entscheidend für Performance-Analysen
- Den **Zeitstempel** — für zeitliche Nutzungsauswertungen

Damit lässt sich z.B. beantworten: "Ist DeepSeek günstiger als OpenAI?" oder "Welche Prompts brauchen besonders lange?"
Das sind reale Fragen aus dem KI-Betrieb, die ohne Audit-Logging nicht zu beantworten wären.
