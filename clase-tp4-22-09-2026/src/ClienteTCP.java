
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteTCP {

    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final int CANTIDAD = 1000;

    public static void main(String[] args) {
        try {
            Resultado json = ejecutarPrueba((byte) 1, CANTIDAD);
            Resultado binario = ejecutarPrueba((byte) 2, CANTIDAD);

            System.out.println("\n========== RESULTADOS ==========");
            imprimirResultado("JSON", CANTIDAD, json);
            imprimirResultado("BINARIO", CANTIDAD, binario);

            System.out.printf(
                    "\nDiferencia de tamaño: %.2f%%%n",
                    porcentajeDiferencia(json.bytes, binario.bytes)
            );

        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }

    private static Resultado ejecutarPrueba(byte formato, int cantidad)
            throws IOException {

        long bytesTotales = 0;
        long inicio = System.nanoTime();

        for (int i = 1; i <= cantidad; i++) {
            Transaccion transaccion = new Transaccion(
                    i,
                    "NodoA",
                    1500.50 + i,
                    System.currentTimeMillis()
            );

            byte[] payload;

            if (formato == 1) {
                String json = ParserMensajes.transaccionAJson(transaccion);
                payload = json.getBytes(StandardCharsets.UTF_8);
            } else {
                payload = ParserMensajes.transaccionABinario(transaccion);
            }

            try (Socket socket = new Socket(HOST, PUERTO);
                 DataOutputStream out = new DataOutputStream(
                         socket.getOutputStream());
                 DataInputStream in = new DataInputStream(
                         socket.getInputStream())) {

                // Encabezado: formato (1 byte) + longitud (4 bytes).
                out.writeByte(formato);
                out.writeInt(payload.length);
                out.write(payload);
                out.flush();

                // Esperar confirmación del servidor.
                boolean confirmado = in.readBoolean();

                if (!confirmado) {
                    throw new IOException(
                            "El servidor no confirmó la transacción " + i);
                }

                bytesTotales += 5L + payload.length;
            }
        }

        long fin = System.nanoTime();

        return new Resultado(bytesTotales, fin - inicio);
    }

    private static void imprimirResultado(String formato,
                                          int cantidad,
                                          Resultado resultado) {
        System.out.println("\nFormato: " + formato);
        System.out.println("Transacciones: " + cantidad);
        System.out.println("Bytes transmitidos: " + resultado.bytes);
        System.out.printf(
                "Tiempo total: %.3f ms%n",
                resultado.nanosegundos / 1_000_000.0
        );
    }

    private static double porcentajeDiferencia(long json, long binario) {
        if (json == 0) {
            return 0;
        }

        return ((double) (json - binario) / json) * 100.0;
    }

    private static class Resultado {
        long bytes;
        long nanosegundos;

        Resultado(long bytes, long nanosegundos) {
            this.bytes = bytes;
            this.nanosegundos = nanosegundos;
        }
    }
}
