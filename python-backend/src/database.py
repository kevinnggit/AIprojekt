"""
Datenbankinitialisierung und Session-Verwaltung mit SQLAlchemy.
Stellt eine wiederverwendbare Datenbankverbindung sowie eine Dependency für FastAPI bereit.
"""

from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from src.config import DATABASE_URL

# Engine ist die Verbindung zur Datenbank – wird einmal erzeugt und dann wiederverwendet
engine = create_engine(DATABASE_URL)

# SessionLocal erzeugt bei jedem Aufruf eine neue Datenbanksitzung
# autocommit=False: Transaktionen müssen explizit committed werden
# autoflush=False: Änderungen werden nicht automatisch in die DB geschrieben
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Basis-Klasse für alle SQLAlchemy-Modelle – Tabellendefinitionen erben davon
Base = declarative_base()


def get_db():
    """
    FastAPI-Dependency, die eine Datenbankverbindung für eine einzelne Anfrage bereitstellt.
    Gibt die Session per yield zurück und schließt sie sicher nach Abschluss der Anfrage.
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        # Session immer schließen, auch bei Ausnahmen – verhindert Connection-Leaks
        db.close()
