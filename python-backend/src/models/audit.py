"""
SQLAlchemy-Modell für den KI-Audit-Log.
Jede KI-Anfrage wird in der Tabelle 'ai_audit_logs' persistiert, um Nachvollziehbarkeit sicherzustellen.
"""

from sqlalchemy import Column, Integer, String, DateTime, Text
from sqlalchemy.sql import func
from src.database import Base


class AuditLog(Base):
    """
    Datenbankmodell für einen einzelnen Audit-Log-Eintrag einer KI-Anfrage.
    Dient der Kostenkontrolle, dem Debugging und der Compliance-Dokumentation.
    """

    __tablename__ = "ai_audit_logs"

    # Primärschlüssel, automatisch inkrementiert
    id = Column(Integer, primary_key=True, index=True)

    # Zeitstempel wird serverseitig von der Datenbank gesetzt (kein Python-Datetime-Objekt nötig)
    timestamp = Column(DateTime(timezone=True), server_default=func.now())

    provider = Column(String)  # z.B. "openai", "deepseek", "ollama"
    model = Column(String)     # z.B. "gpt-3.5-turbo", "deepseek-chat"

    # Nur die ersten 500 Zeichen des Prompts werden gespeichert (Datenschutz und Effizienz)
    prompt_preview = Column(Text)
    response_preview = Column(Text)

    latency_ms = Column(Integer)  # Antwortzeit in Millisekunden – für Performance-Tracking
