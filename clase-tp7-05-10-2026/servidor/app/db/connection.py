from collections.abc import Iterator
from contextlib import contextmanager
from pathlib import Path
import sqlite3

BASE_DIR = Path(__file__).resolve().parent.parent.parent
DATABASE_PATH = BASE_DIR / "data" / "reservas.db"


@contextmanager
def obtener_conexion() -> Iterator[sqlite3.Connection]:
    DATABASE_PATH.parent.mkdir(parents=True, exist_ok=True)

    conexion = sqlite3.connect(DATABASE_PATH)
    conexion.row_factory = sqlite3.Row
    conexion.execute("PRAGMA foreign_keys = ON")

    try:
        yield conexion
        conexion.commit()
    except Exception:
        try:
            conexion.rollback()
        except sqlite3.Error:
            pass
        raise
    finally:
        conexion.close()
