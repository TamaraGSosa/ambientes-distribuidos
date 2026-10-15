
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class EmisorUDP {

    private static final String HOST = "localhost";
    private static final int PUERTO = 6000;

    public static void main(String[] args) {

        System.out.println("Iniciando emisor UDP...");

        try (DatagramSocket socket = new DatagramSocket()) {

            InetAddress direccionServidor =
                InetAddress.getByName(HOST);

            int contador = 1;

            while (true) {

                String mensaje =
                    "Alerta de telemetria numero " + contador;

                byte[] datos =
                    mensaje.getBytes(StandardCharsets.UTF_8);

                DatagramPacket paquete = new DatagramPacket(
                    datos,
                    datos.length,
                    direccionServidor,
                    PUERTO
                );

                socket.send(paquete);

                System.out.println("Alerta enviada: " + mensaje);

                contador++;

                Thread.sleep(3000);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Emisor interrumpido.");

        } catch (IOException e) {
            System.err.println(
                "Error en el emisor UDP: " + e.getMessage()
            );
        }
    }
}
