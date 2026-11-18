import sqlite3
from app.db.connection import obtener_conexion
from app.models.cancha import Cancha


class CanchaRepository:
    @staticmethod
    def _fila_a_modelo(fila: sqlite3.Row) -> Cancha:
        return Cancha(
            id=fila["id"],
            nombre=fila["nombre"],
            deporte=fila["deporte"],
            precio_hora=fila["precio_hora"],
            activa=bool(fila["activa"]),
        )

    def listar_activas(self) -> list[Cancha]:
        with obtener_conexion() as conexion:
            cursor = conexion.execute(
                """
                SELECT id, nombre, deporte, precio_hora, activa
                FROM canchas
                WHERE activa = 1
                ORDER BY id ASC
                """
            )
            filas = cursor.fetchall()
            return [self._fila_a_modelo(fila) for fila in filas]

    def obtener_por_id(self, cancha_id: int) -> Cancha | None:
        with obtener_conexion() as conexion:
            cursor = conexion.execute(
                """
                SELECT id, nombre, deporte, precio_hora, activa
                FROM canchas
                WHERE id = ?
                """,
                (cancha_id,),
            )
            fila = cursor.fetchone()
            return self._fila_a_modelo(fila) if fila else None

