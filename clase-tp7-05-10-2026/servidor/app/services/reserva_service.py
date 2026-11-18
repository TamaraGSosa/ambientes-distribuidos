from datetime import date
from fastapi import HTTPException, status

from app.models.reserva import Reserva
from app.repositories.cancha_repository import CanchaRepository
from app.repositories.reserva_repository import ReservaRepository
from app.schemas.reserva import ReservaCreate


class ReservaService:
    def __init__(
        self,
        reserva_repository: ReservaRepository | None = None,
        cancha_repository: CanchaRepository | None = None,
    ) -> None:
        self.reserva_repository = reserva_repository or ReservaRepository()
        self.cancha_repository = cancha_repository or CanchaRepository()

    def listar_reservas(self) -> list[Reserva]:
        return self.reserva_repository.listar_todas()

    def obtener_reserva(self, reserva_id: int) -> Reserva:
        reserva = self.reserva_repository.obtener_por_id(reserva_id)
        if reserva is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"No se encontró la reserva con ID {reserva_id}",
            )
        return reserva

    def crear_reserva(self, datos: ReservaCreate) -> Reserva:
        cancha = self.cancha_repository.obtener_por_id(datos.cancha_id)
        if cancha is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"No existe una cancha con ID {datos.cancha_id}",
            )

        if not cancha.activa:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"La cancha '{cancha.nombre}' no se encuentra activa",
            )

        if datos.fecha < date.today():
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="No se permiten reservas en fechas pasadas",
            )

        if datos.hora_inicio >= datos.hora_fin:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="La hora de inicio debe ser anterior a la hora de fin",
            )

        fecha_str = datos.fecha.isoformat()
        hora_inicio_str = datos.hora_inicio.strftime("%H:%M")
        hora_fin_str = datos.hora_fin.strftime("%H:%M")

        if self.reserva_repository.existe_superposicion(
            cancha_id=datos.cancha_id,
            fecha=fecha_str,
            hora_inicio=hora_inicio_str,
            hora_fin=hora_fin_str,
        ):
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="El horario solicitado se superpone con una reserva existente para esa cancha y fecha",
            )

        return self.reserva_repository.crear(
            cancha_id=datos.cancha_id,
            cliente=datos.cliente,
            fecha=fecha_str,
            hora_inicio=hora_inicio_str,
            hora_fin=hora_fin_str,
        )

