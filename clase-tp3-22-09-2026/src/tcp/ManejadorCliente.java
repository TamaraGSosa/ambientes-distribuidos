
package tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ManejadorCliente extends Thread {

    private final Socket socket;
    private final BufferedReader entrada;
    private final BufferedWriter salida;
    private final AtomicBoolean cerrado = new AtomicBoolean(false);

    public ManejadorCliente(Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new BufferedReader(
            new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8
            )
        );
        this.salida = new BufferedWriter(
            new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8
            )
        );
    }

    @Override
    public void run() {
        try {
            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Mensaje recibido: " + mensaje);

                ServidorTCP.difundirMensaje(
                    this, mensaje
                );
            }

        } catch (IOException e) {
            if (!cerrado.get()) {
                System.out.println(
                    "Se cerró la conexión con un cliente: "
                    + e.getMessage()
                );
            }

        } finally {
            cerrar();
        }
    }

    public synchronized void enviarMensaje(String mensaje)
            throws IOException {
        if (cerrado.get()) {
            throw new IOException("El cliente ya está desconectado.");
        }

        salida.write(mensaje);
        salida.newLine();
        salida.flush();
    }

    public void cerrar() {
        if (!cerrado.compareAndSet(false, true)) {
            return;
        }

        ServidorTCP.eliminarCliente(this);

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(
                "Error al cerrar el socket: " + e.getMessage()
            );
        }
    }
}
