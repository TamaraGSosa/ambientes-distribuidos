from locust import LoadTestShape

from locustfile import TP8DistributedUser  # noqa: F401


class StagedStressShape(LoadTestShape):
    """
    Rampa progresiva por escalones de 36 segundos cada uno (180 s totales):
    50 -> 100 -> 150 -> 200 -> 250 usuarios concurrentes.
    """

    stages = [
        {"duration": 36, "users": 50, "spawn_rate": 50},
        {"duration": 72, "users": 100, "spawn_rate": 25},
        {"duration": 108, "users": 150, "spawn_rate": 25},
        {"duration": 144, "users": 200, "spawn_rate": 25},
        {"duration": 180, "users": 250, "spawn_rate": 25},
    ]

    def tick(self):
        run_time = self.get_run_time()

        for stage in self.stages:
            if run_time < stage["duration"]:
                return (stage["users"], stage["spawn_rate"])

        return None
