from fastapi import APIRouter, status

from app.controllers.cancha_controller import CanchaController
from app.schemas.cancha import CanchaResponse

router = APIRouter(prefix="/canchas", tags=["Canchas"])
cancha_controller = CanchaController()


@router.get("", response_model=list[CanchaResponse], status_code=status.HTTP_200_OK)
def listar_canchas() -> list[CanchaResponse]:
    return cancha_controller.listar_canchas_activas()

