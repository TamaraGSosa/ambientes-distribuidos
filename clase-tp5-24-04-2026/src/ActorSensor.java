public class ActorSensor extends Thread {

    private final int id;
    private final ActorProcesador procesador;

    public ActorSensor(int id, ActorProcesador procesador) {
        this.id = id;
        this.procesador = procesador;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            double temperatura = 20 + Math.random() * 15;

            procesador.enviar(new Lectura(id, temperatura));
        }
    }
}