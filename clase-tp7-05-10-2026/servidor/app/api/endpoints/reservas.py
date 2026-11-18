from fastapi import APIRouter, status

from app.controllers.reserva_controller import ReservaController
from app.schemas.reserva import ReservaCreate, ReservaResponse

router = APIRouter(prefix="/reservas", tags=["Reservas"])
reserva_controller = ReservaController()


@router.get("", response_model=list[ReservaResponse], status_code=status.HTTP_200_OK)
def listar_reservas() -> list[ReservaResponse]:
    return reserva_controller.listar_reservas()


@router.get(
    "/{id}",
    response_model=ReservaResponse,
    status_code=status.HTTP_200_OK,
)
def obtener_reserva(id: int) -> ReservaResponse:
    return reserva_controller.obtener_reserva(id)


@router.post(
    "",
    response_model=ReservaResponse,
    status_code=status.HTTP_201_CREATED,
)
def crear_reserva(datos: ReservaCreate) -> ReservaResponse:
    return reserva_controller.crear_reserva(datos)

