from app.schemas.reserva import ReservaCreate, ReservaResponse
from app.services.reserva_service import ReservaService


class ReservaController:
    def __init__(self, reserva_service: ReservaService | None = None) -> None:
        self.reserva_service = reserva_service or ReservaService()

    def listar_reservas(self) -> list[ReservaResponse]:
        reservas = self.reserva_service.listar_reservas()
        return [ReservaResponse.model_validate(reserva) for reserva in reservas]

    def obtener_reserva(self, reserva_id: int) -> ReservaResponse:
        reserva = self.reserva_service.obtener_reserva(reserva_id)
        return ReservaResponse.model_validate(reserva)

    def crear_reserva(self, datos: ReservaCreate) -> ReservaResponse:
        reserva = self.reserva_service.crear_reserva(datos)
        return ReservaResponse.model_validate(reserva)

