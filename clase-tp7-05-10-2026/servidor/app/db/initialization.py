from app.db.connection import obtener_conexion


def inicializar_base_datos() -> None:
    with obtener_conexion() as conexion:
        conexion.execute("""
            CREATE TABLE IF NOT EXISTS canchas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                deporte TEXT NOT NULL,
                precio_hora REAL NOT NULL CHECK (precio_hora >= 0),
                activa INTEGER NOT NULL DEFAULT 1
            )
        """)

        conexion.execute("""
            CREATE TABLE IF NOT EXISTS reservas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cancha_id INTEGER NOT NULL,
                cliente TEXT NOT NULL,
                fecha TEXT NOT NULL,
                hora_inicio TEXT NOT NULL,
                hora_fin TEXT NOT NULL,
                FOREIGN KEY (cancha_id) REFERENCES canchas(id)
            )
        """)

        cantidad = conexion.execute(
            "SELECT COUNT(*) FROM canchas"
        ).fetchone()[0]

        if cantidad == 0:
            conexion.executemany(
                """
                INSERT INTO canchas (nombre, deporte, precio_hora)
                VALUES (?, ?, ?)
                """,
                [
                    ("Cancha 1", "Fútbol 5", 25000),
                    ("Cancha 2", "Fútbol 5", 28000),
                    ("Cancha 3", "Pádel", 18000),
                ],
            )

