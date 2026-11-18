from dataclasses import dataclass


@dataclass
class Reserva:
    id: int
    cancha_id: int
    cliente: str
    fecha: str
    hora_inicio: str
    hora_fin: str

