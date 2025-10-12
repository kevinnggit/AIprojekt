from time import perf_counter
from typing import Any, Dict

from openai import OpenAI
from ..config import OPENAI_API_KEY, OPENAI_MODEL, SYSTEM_PROMPT

_client: OpenAI | None = None

def get_client() -> OpenAI:
    global _client
    if _client is None:
        if not OPENAI_API_KEY:
            raise RuntimeError("OPENAI_API_KEY not set")
        _client = OpenAI(api_key=OPENAI_API_KEY)
    return _client

def infer_text(text: str) -> Dict[str, Any]:
    client = get_client()
    t0 = perf_counter()
    resp = client.chat.completions.create(
        model=OPENAI_MODEL,
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": text}
        ],
        temperature=0.2,
    )
    latency_ms = int((perf_counter() - t0) * 1000)
    choice = resp.choices[0].message
    return {
        "model": OPENAI_MODEL,
        "provider": "openai",
        "result": {
            "role": choice.role,
            "content": choice.content
        },
        "latency_ms": latency_ms
    }