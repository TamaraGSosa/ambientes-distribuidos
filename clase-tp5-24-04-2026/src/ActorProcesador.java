import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActorProcesador extends Thread {

    private final BlockingQueue<Lectura> buzon =
            new LinkedBlockingQueue<>();

    private int contador = 0;
    private double suma = 0;

    public void enviar(Lectura lectura) {
        buzon.add(lectura);
    }

    @Override
    public void run() {
        try {
            while (contador < 500) {
                Lectura lectura = buzon.take();

                contador++;
                suma += lectura.getTemperatura();

                System.out.println(
                    "Mensaje " + contador
                    + " | Sensor: " + lectura.getSensorId()
                    + " | Temperatura: "
                    + String.format("%.2f", lectura.getTemperatura())
                );
            }

            System.out.println("\n--- RESULTADO FINAL ---");
            System.out.println("Mensajes procesados: " + contador);
            System.out.printf("Promedio final: %.2f%n", suma / contador);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}