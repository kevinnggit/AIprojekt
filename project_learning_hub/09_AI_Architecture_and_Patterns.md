# 09. AI Architecture & Patterns (Sprint 9)

Willkommen zur Revolution.
Wir haben unser System von einem "One-Trick-Pony" (nur OpenAI) zu einem flexiblen **Multi-Provider System** umgebaut.
Das Zauberwort heißt: **Decoupling**.

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
# 🍝 Spaghetti Code bei jedem neuen Provider!
```

### Die Lösung: Factory
Eine zentrale Fabrik (`ProviderFactory`), die entscheidet, was erstellt wird.
```python
# Der Service sagt nur: "Gib mir 'deepseek'!"
provider = ProviderFactory.get_provider("deepseek")
```
Die Factory kümmert sich um API-Keys, Instanziierung und Caching (Singleton-ähnlich).

## 2. Polymorphism & Abstract Base Classes (ABC)
Damit die Factory funktionieren kann, müssen alle Provider **gleich aussehen**.
Sie müssen denselben "Vertrag" erfüllen.

In Python nutzen wir `ABC` (Abstract Base Classes):
```python
class AIProvider(ABC):
    @abstractmethod
    def generate(self, prompt):
        pass
```
Jeder Provider (`OpenAIProvider`, `DeepSeekProvider`) **MUSS** diese `generate`-Methode implementieren.
Das nennt man **Polymorphismus**: Dem Service ist es egal, *welches* Objekt er hat, solange es `generate()` kann.

### DeepSeek & OpenAI
Da DeepSeek eine zu OpenAI kompatible API hat, nutzen wir Vererbung:
`DeepSeekProvider` erbt von `OpenAICompatibleProvider`. Wir ändern nur die `base_url` auf `https://api.deepseek.com`.
-> **Code Reuse at its finest.** 🧠

## 3. Frontend-Backend Handshake
Wie weiß der Server, was der User will?

1.  **Frontend (`KiPython.vue`):** Dropdown ändert Reactive State `provider`.
2.  **API Call:** `fetch(..., body: { provider: 'deepseek' })`
3.  **Router (`ai_router.py`):** Liest `req.provider`.
4.  **Service:** `factory.get_provider(req.provider).generate(...)`

Ein perfekter Fluss von der UI bis zur Architektur-Ebene.
