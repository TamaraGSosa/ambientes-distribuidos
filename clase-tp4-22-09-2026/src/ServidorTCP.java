
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServidorTCP {

    private static final int PUERTO = 5000;
    private static final int MAX_MENSAJE = 1_000_000;

    public static void main(String[] args) {
        System.out.println("Servidor TCP iniciado en el puerto " + PUERTO);

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            while (true) {
                try (Socket cliente = servidor.accept();
                     DataInputStream in = new DataInputStream(
                             new BufferedInputStream(
                                     cliente.getInputStream()));
                     DataOutputStream out = new DataOutputStream(
                             cliente.getOutputStream())) {

                    System.out.println("\nCliente conectado: "
                            + cliente.getInetAddress());

                    // El cliente indica el formato y la longitud.
                    byte formato = in.readByte();
                    int longitud = in.readInt();

                    if (longitud < 0 || longitud > MAX_MENSAJE) {
                        throw new IOException("Longitud inválida: " + longitud);
                    }

                    byte[] datos = new byte[longitud];
                    in.readFully(datos);

                    Transaccion transaccion;

                    if (formato == 1) {
                        String json = new String(
                                datos, StandardCharsets.UTF_8);
                        transaccion =
                                ParserMensajes.jsonATransaccion(json);

                    } else if (formato == 2) {
                        transaccion =
                                ParserMensajes.binarioATransaccion(datos);

                    } else {
                        throw new IOException(
                                "Formato desconocido: " + formato);
                    }

                    System.out.println("Formato recibido: "
                            + (formato == 1 ? "JSON" : "BINARIO"));
                    System.out.println("Datos reconstruidos: " + transaccion);

                    out.writeBoolean(true);
                    out.flush();

                } catch (IOException | IllegalArgumentException e) {
                    System.err.println("Error al procesar cliente: "
                            + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: "
                    + e.getMessage());
        }
    }
}
