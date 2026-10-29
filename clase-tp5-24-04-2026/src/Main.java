public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        ActorProcesador procesador = new ActorProcesador();
        procesador.start();

        ActorSensor[] sensores = new ActorSensor[10];

        for (int i = 0; i < 10; i++) {
            sensores[i] = new ActorSensor(i + 1, procesador);
            sensores[i].start();
        }

        for (ActorSensor sensor : sensores) {
            sensor.join();
        }

        procesador.join();

        System.out.println("Programa finalizado.");
    }
}