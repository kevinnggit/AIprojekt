FROM python:3.11-slim

WORKDIR /app

# Installiere System-Abhängigkeiten
RUN apt-get update && apt-get install -y \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# Kopiere requirements und installiere Python-Abhängigkeiten
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Kopiere den gesamten src Ordner
COPY src/ ./src/
COPY tests/ ./tests/

# Setze PYTHONPATH
ENV PYTHONPATH=/app
ENV PYTHONUNBUFFERED=1

# Starte die Anwendung
CMD ["python", "-m", "src.main"]