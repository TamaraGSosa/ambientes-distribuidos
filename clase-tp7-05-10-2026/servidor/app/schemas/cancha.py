from pydantic import BaseModel, ConfigDict


class CanchaResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    nombre: str
    deporte: str
    precio_hora: float
    activa: bool

