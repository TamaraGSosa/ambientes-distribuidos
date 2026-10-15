
package tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteTCP {

    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {

        try (
            Socket socket = new Socket(HOST, PUERTO);

            BufferedReader teclado = new BufferedReader(
                new InputStreamReader(System.in)
            );

            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.UTF_8
                )
            );

            BufferedWriter salida = new BufferedWriter(
                new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8
                )
            )
        ) {
            System.out.println("Conectado al chat.");
            System.out.println("Escribí /salir para desconectarte.");

            Thread receptor = new Thread(() -> {
                try {
                    String mensaje;

                    while ((mensaje = entrada.readLine()) != null) {
                        System.out.println(mensaje);
                    }

                } catch (IOException e) {
                    System.out.println("Conexión cerrada.");
                }
            });

            receptor.setDaemon(true);
            receptor.start();

            String mensaje;

            while ((mensaje = teclado.readLine()) != null) {

                if (mensaje.equalsIgnoreCase("/salir")) {
                    break;
                }

                salida.write(mensaje);
                salida.newLine();
                salida.flush();
            }

        } catch (IOException e) {
            System.err.println(
                "No se pudo conectar: " + e.getMessage()
            );
        }

        System.out.println("Cliente desconectado.");
    }
}
