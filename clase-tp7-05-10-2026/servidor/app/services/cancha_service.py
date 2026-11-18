from app.models.cancha import Cancha
from app.repositories.cancha_repository import CanchaRepository


class CanchaService:
    def __init__(self, cancha_repository: CanchaRepository | None = None) -> None:
        self.cancha_repository = cancha_repository or CanchaRepository()

    def listar_canchas_activas(self) -> list[Cancha]:
        return self.cancha_repository.listar_activas()

