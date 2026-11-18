from dataclasses import dataclass


@dataclass
class Cancha:
    id: int
    nombre: str
    deporte: str
    precio_hora: float
    activa: bool

