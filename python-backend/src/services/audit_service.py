"""
Service zur persistenten Protokollierung aller KI-Anfragen in der Datenbank.
Jeder Aufruf schreibt einen Eintrag in die Audit-Log-Tabelle für Nachvollziehbarkeit und Compliance.
"""

from src.database import SessionLocal
from src.models.audit import AuditLog


class AuditService:
    """
    Statischer Service für das Audit-Logging von KI-Anfragen.
    Alle Methoden arbeiten ohne Instanz und öffnen/schließen ihre eigene Datenbankverbindung.
    """

    @staticmethod
    def log_request(provider: str, model: str, prompt: str, response: str, latency_ms: int):
        """
        Schreibt einen Audit-Log-Eintrag für eine KI-Anfrage in die Datenbank.
        Bei Fehlern wird die Transaktion zurückgerollt, ohne die laufende Anfrage zu unterbrechen.

        :param provider: Name des verwendeten Providers (z.B. "openai")
        :param model: Name des verwendeten Modells (z.B. "gpt-3.5-turbo")
        :param prompt: Der gesendete Prompt (wird auf 500 Zeichen gekürzt)
        :param response: Die empfangene Antwort (wird auf 500 Zeichen gekürzt)
        :param latency_ms: Gemessene Antwortzeit in Millisekunden
        """
        db = SessionLocal()
        try:
            # Prompt und Antwort werden auf 500 Zeichen begrenzt – ausreichend für Debugging,
            # schont aber Speicherplatz und schützt vor sehr langen Eingaben
            log_entry = AuditLog(
                provider=provider,
                model=model,
                prompt_preview=prompt[:500],
                response_preview=response[:500],
                latency_ms=latency_ms
            )
            db.add(log_entry)
            db.commit()
            # Eintrag neu laden, um die vom Datenbankserver gesetzten Werte (z.B. id, timestamp) zu erhalten
            db.refresh(log_entry)
        except Exception as e:
            print(f"Error logging audit: {e}")
            # Transaktion zurückrollen, um die DB in einem konsistenten Zustand zu lassen
            db.rollback()
        finally:
            # Session immer schließen, auch wenn ein Fehler aufgetreten ist
            db.close()
