from contextlib import asynccontextmanager
from fastapi import FastAPI

from app.api.endpoints import canchas, reservas
from app.db.initialization import inicializar_base_datos


@asynccontextmanager
async def lifespan(_: FastAPI):
    inicializar_base_datos()
    yield


app = FastAPI(
    title="ReservaCancha API",
    description="API REST para gestionar reservas de canchas deportivas",
    version="1.0.0",
    lifespan=lifespan,
)


@app.get("/", tags=["General"])
def inicio():
    return {
        "mensaje": "Bienvenido a ReservaCancha API",
        "estado": "activo",
    }


@app.get("/salud", tags=["General"])
def verificar_salud():
    return {
        "estado": "ok",
        "servicio": "ReservaCancha API",
    }


app.include_router(canchas.router)
app.include_router(reservas.router)

