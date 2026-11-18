from app.schemas.cancha import CanchaResponse
from app.services.cancha_service import CanchaService


class CanchaController:
    def __init__(self, cancha_service: CanchaService | None = None) -> None:
        self.cancha_service = cancha_service or CanchaService()

    def listar_canchas_activas(self) -> list[CanchaResponse]:
        canchas = self.cancha_service.listar_canchas_activas()
        return [CanchaResponse.model_validate(cancha) for cancha in canchas]

