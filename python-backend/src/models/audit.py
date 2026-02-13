from sqlalchemy import Column, Integer, String, DateTime, Text
from sqlalchemy.sql import func
from src.database import Base

class AuditLog(Base):
    __tablename__ = "ai_audit_logs"

    # 📊 AUDIT SCHEMA
    # Speichert Metadaten für jede KI-Anfrage.
    # Wichtig für Kostenkontrolle, Debugging und Compliance.
    id = Column(Integer, primary_key=True, index=True)
    timestamp = Column(DateTime(timezone=True), server_default=func.now())
    provider = Column(String) # z.B. "openai", "deepseek"
    model = Column(String) 
    prompt_preview = Column(Text) # Nur Preview speichern (Datenschutz!)
    response_preview = Column(Text) 
    latency_ms = Column(Integer) # Performance-Tracking
