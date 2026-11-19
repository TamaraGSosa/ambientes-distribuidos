from locust import HttpUser, between, task


class TP8DistributedUser(HttpUser):
    """
    Simula usuarios concurrentes contra la API de TP8 (http://localhost:3000).
    Prioriza lecturas idempotentes sobre postgres-replica (/records) y verificación
    multi-nodo (/health), sin mutar ni ensuciar la base de datos.
    """

    wait_time = between(0.1, 0.5)

    @task(8)
    def listar_records(self):
        with self.client.get("/records", name="GET /records", catch_response=True) as resp:
            if resp.status_code == 200:
                resp.success()
            else:
                resp.failure(f"HTTP {resp.status_code}: {resp.text[:120]}")

    @task(2)
    def verificar_salud(self):
        with self.client.get("/health", name="GET /health", catch_response=True) as resp:
            if resp.status_code == 200:
                resp.success()
            else:
                resp.failure(f"HTTP {resp.status_code}: {resp.text[:120]}")
