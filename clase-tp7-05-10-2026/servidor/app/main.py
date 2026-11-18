from contextlib import asynccontextmanager
import logging
import os

from fastapi import FastAPI, HTTPException, Request, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.api.endpoints import canchas, reservas
from app.db.initialization import inicializar_base_datos

logger = logging.getLogger(__name__)

DEFAULT_CORS_ORIGINS = [
    "http://localhost:5173",
    "http://127.0.0.1:5173",
    "http://172.20.160.1:5173",
]


def obtener_origenes_cors() -> list[str]:
    env_origins = os.getenv("CORS_ORIGINS", "").strip()
    if not env_origins:
        return list(DEFAULT_CORS_ORIGINS)
    extra = [
        item.strip()
        for item in env_origins.split(",")
        if item.strip() and item.strip() != "*"
    ]
    return list(dict.fromkeys([*DEFAULT_CORS_ORIGINS, *extra]))


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

app.add_middleware(
    CORSMiddleware,
    allow_origins=obtener_origenes_cors(),
    allow_credentials=False,
    allow_methods=["GET", "POST", "OPTIONS"],
    allow_headers=["Content-Type", "Accept"],
)


@app.exception_handler(Exception)
async def manejar_error_interno(request: Request, exc: Exception) -> JSONResponse:
    if isinstance(exc, HTTPException):
        return JSONResponse(
            status_code=exc.status_code,
            content={"detail": exc.detail},
            headers=exc.headers,
        )
    logger.exception("Error interno no controlado en %s %s", request.method, request.url.path)
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={"detail": "Error interno del servidor"},
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
