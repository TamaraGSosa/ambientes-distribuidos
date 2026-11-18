import sqlite3
from app.db.connection import obtener_conexion
from app.models.reserva import Reserva


class ReservaRepository:
    @staticmethod
    def _fila_a_modelo(fila: sqlite3.Row) -> Reserva:
        return Reserva(
            id=fila["id"],
            cancha_id=fila["cancha_id"],
            cliente=fila["cliente"],
            fecha=fila["fecha"],
            hora_inicio=fila["hora_inicio"],
            hora_fin=fila["hora_fin"],
        )

    def listar_todas(self) -> list[Reserva]:
        with obtener_conexion() as conexion:
            cursor = conexion.execute(
                """
                SELECT id, cancha_id, cliente, fecha, hora_inicio, hora_fin
                FROM reservas
                ORDER BY fecha ASC, hora_inicio ASC, id ASC
                """
            )
            filas = cursor.fetchall()
            return [self._fila_a_modelo(fila) for fila in filas]

    def obtener_por_id(self, reserva_id: int) -> Reserva | None:
        with obtener_conexion() as conexion:
            cursor = conexion.execute(
                """
                SELECT id, cancha_id, cliente, fecha, hora_inicio, hora_fin
                FROM reservas
                WHERE id = ?
                """,
                (reserva_id,),
            )
            fila = cursor.fetchone()
            return self._fila_a_modelo(fila) if fila else None

    def existe_superposicion(
        self, cancha_id: int, fecha: str, hora_inicio: str, hora_fin: str
    ) -> bool:
        with obtener_conexion() as conexion:
            cursor = conexion.execute(
                """
                SELECT COUNT(*)
                FROM reservas
                WHERE cancha_id = ?
                  AND fecha = ?
                  AND hora_inicio < ?
                  AND hora_fin > ?
                """,
                (cancha_id, fecha, hora_fin, hora_inicio),
            )
            cantidad = cursor.fetchone()[0]
            return cantidad > 0

    def crear(
        self,
        cancha_id: int,
        cliente: str,
        fecha: str,
        hora_inicio: str,
        hora_fin: str,
    ) -> Reserva:
        with obtener_conexion() as conexion:
            cursor = conexion.execute(
                """
                INSERT INTO reservas (cancha_id, cliente, fecha, hora_inicio, hora_fin)
                VALUES (?, ?, ?, ?, ?)
                """,
                (cancha_id, cliente, fecha, hora_inicio, hora_fin),
            )
            nuevo_id = cursor.lastrowid
            return Reserva(
                id=nuevo_id,
                cancha_id=cancha_id,
                cliente=cliente,
                fecha=fecha,
                hora_inicio=hora_inicio,
                hora_fin=hora_fin,
            )

