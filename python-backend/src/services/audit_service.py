from src.database import SessionLocal
from src.models.audit import AuditLog

class AuditService:
    @staticmethod
    def log_request(provider: str, model: str, prompt: str, response: str, latency_ms: int):
        db = SessionLocal()
        try:
            # Truncate previews to 200 chars to save space in text preview, 
            # though column is Text so it fits, but good practice for 'preview'
            log_entry = AuditLog(
                provider=provider,
                model=model,
                prompt_preview=prompt[:500],
                response_preview=response[:500],
                latency_ms=latency_ms
            )
            db.add(log_entry)
            db.commit()
            db.refresh(log_entry)
        except Exception as e:
            print(f"Error logging audit: {e}")
            db.rollback()
        finally:
            db.close()
