import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class ClienteResiliente {

    // Configuración de reintentos
    private static final int MAX_INTENTOS = 5;
    private static final long BASE = 1000; // 1000 ms = 1 segundo

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        // Pedimos los datos al usuario
        System.out.print("Ingrese el primer numero: ");
        int numero1 = scanner.nextInt();

        System.out.print("Ingrese la operacion (+, -, *, /): ");
        String operacion = scanner.next();

        System.out.print("Ingrese el segundo numero: ");
        int numero2 = scanner.nextInt();

        // Armamos el mensaje
        String mensaje = numero1 + ";" + operacion + ";" + numero2;

        // MÉTRICAS
        int intentosRealizados = 0;
        boolean exito = false;

        // Comenzamos a medir el tiempo
        long inicio = System.nanoTime();

        // REINTENTOS
        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {

            intentosRealizados++;

            System.out.println("\nIntento " + intento + " de " + MAX_INTENTOS);

            try {

                // Nos conectamos al servidor
                Socket client = new Socket("localhost", 5500);

                // Permite enviar informacion al servidor
                PrintWriter output =
                        new PrintWriter(client.getOutputStream(), true);

                // Enviamos el mensaje al servidor
                output.println(mensaje);

                // Recibimos la respuesta del servidor
                BufferedReader input =
                        new BufferedReader(
                                new InputStreamReader(
                                        client.getInputStream()));

                // Guardamos la respuesta
                String message = input.readLine();

                // Mostramos la respuesta
                System.out.println("Respuesta del servidor: " + message);

                // Cerramos la conexion
                client.close();

                // La petición fue exitosa
                exito = true;

                // Salimos del ciclo porque ya funcionO
                break;

            } catch (IOException e) {

                System.out.println("Error de comunicación con el servidor.");

                // Si quedan intentos, esperamos antes de volver a intentar
                if (intento < MAX_INTENTOS) {

                    // Backoff Exponencial
                    long backoff = BASE * (long) Math.pow(2, intento - 1);

                    // Jitter aleatorio entre 0 y 500 ms
                    long jitter =
                            ThreadLocalRandom.current().nextLong(0, 501);

                    // Tiempo de espera = Backoff + Jitter
                    long tiempoEspera = backoff + jitter;

                    System.out.println(
                            "Esperando " + tiempoEspera +
                            " ms antes del próximo intento..."
                    );

                    try {
                        Thread.sleep(tiempoEspera);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // Finalizamos la mediciOn del tiempo
        long fin = System.nanoTime();

        long tiempoTotalMs = (fin - inicio) / 1_000_000;

        // MOSTRAMOS LAS MÉTRICAS
        System.out.println("\n========== MÉTRICAS DE RESILIENCIA ==========");

        if (exito) {
            System.out.println("Estado final: EXITO");
        } else {
            System.out.println("Estado final: FALLO ");
        }

        System.out.println("Intentos realizados: " + intentosRealizados);
        System.out.println(
                "Tiempo total transcurrido: " + tiempoTotalMs + " ms"
        );

        System.out.println("=================");

        scanner.close();
    }
}