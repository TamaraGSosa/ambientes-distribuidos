from datetime import date, time
from pydantic import BaseModel, ConfigDict, Field, field_validator


class ReservaCreate(BaseModel):
    cancha_id: int = Field(..., gt=0, description="ID de la cancha a reservar")
    cliente: str = Field(..., min_length=1, description="Nombre del cliente")
    fecha: date = Field(..., description="Fecha de la reserva (YYYY-MM-DD)")
    hora_inicio: time = Field(..., description="Hora de inicio (HH:MM)")
    hora_fin: time = Field(..., description="Hora de fin (HH:MM)")

    @field_validator("cliente")
    @classmethod
    def validar_cliente_no_vacio(cls, valor: str) -> str:
        limpio = valor.strip()
        if not limpio:
            raise ValueError("El nombre del cliente no puede estar vacío")
        return limpio


class ReservaResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    cancha_id: int
    cliente: str
    fecha: str
    hora_inicio: str
    hora_fin: str

