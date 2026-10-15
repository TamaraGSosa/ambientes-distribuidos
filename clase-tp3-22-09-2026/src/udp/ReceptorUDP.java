
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class ReceptorUDP {

    private static final int PUERTO = 6000;
    private static final int TAMANIO_BUFFER = 1024;

    public static void main(String[] args) {

        System.out.println("Iniciando receptor UDP...");

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {

            // Espera máxima de 5 segundos.
            socket.setSoTimeout(5000);

            System.out.println(
                "Esperando alertas en el puerto " + PUERTO
            );

            while (true) {

                byte[] buffer = new byte[TAMANIO_BUFFER];

                DatagramPacket paquete = new DatagramPacket(
                    buffer, buffer.length
                );

                try {
                    socket.receive(paquete);

                    String mensaje = new String(
                        paquete.getData(),
                        paquete.getOffset(),
                        paquete.getLength(),
                        StandardCharsets.UTF_8
                    );

                    System.out.println(
                        "Alerta recibida: " + mensaje
                    );

                } catch (SocketTimeoutException e) {
                    System.out.println(
                        "ADVERTENCIA: No se recibieron alertas "
                        + "durante 5 segundos."
                    );
                }
            }

        } catch (IOException e) {
            System.err.println(
                "Error en el receptor UDP: " + e.getMessage()
            );
        }
    }
}
