
package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorTCP {

    private static final int PUERTO = 5000;

    private static final Set<ManejadorCliente> clientes =
        ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {

        System.out.println("Iniciando servidor TCP...");

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            System.out.println(
                "Servidor escuchando en el puerto " + PUERTO
            );

            while (true) {
                Socket socketCliente = servidor.accept();

                try {
                    ManejadorCliente cliente =
                        new ManejadorCliente(socketCliente);

                    clientes.add(cliente);
                    cliente.start();

                    System.out.println(
                        "Cliente conectado. Total: " + clientes.size()
                    );
                } catch (IOException e) {
                    System.err.println(
                        "No se pudo inicializar un cliente: "
                        + e.getMessage()
                    );

                    try {
                        socketCliente.close();
                    } catch (IOException cierreError) {
                        System.err.println(
                            "Error al cerrar el socket del cliente: "
                            + cierreError.getMessage()
                        );
                    }
                }
            }

        } catch (IOException e) {
            System.err.println(
                "Error en el servidor: " + e.getMessage()
            );
        }
    }

    public static void difundirMensaje(
        ManejadorCliente emisor, String mensaje) {

        for (ManejadorCliente cliente : clientes) {
            if (cliente != emisor) {
                try {
                    cliente.enviarMensaje(mensaje);
                } catch (IOException e) {
                    System.err.println(
                        "Error al enviar a un cliente: " + e.getMessage()
                    );
                    cliente.cerrar();
                }
            }
        }
    }

    public static void eliminarCliente(ManejadorCliente cliente) {
        clientes.remove(cliente);

        System.out.println(
            "Cliente desconectado. Total: " + clientes.size()
        );
    }
}
