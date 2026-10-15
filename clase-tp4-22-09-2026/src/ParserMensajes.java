
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserMensajes {

    // Convierte una transacción a JSON.
    public static String transaccionAJson(Transaccion t) {
        return String.format(
                java.util.Locale.US,
                "{\"idTransaccion\":%d,\"origen\":\"%s\",\"monto\":%.2f,\"timestamp\":%d}",
                t.getIdTransaccion(),
                escaparJson(t.getOrigen()),
                t.getMonto(),
                t.getTimestamp()
        );
    }

    // Reconstruye una transacción desde JSON.
    public static Transaccion jsonATransaccion(String json) {
        Pattern patron = Pattern.compile(
                "\\{\"idTransaccion\":(-?\\d+),\"origen\":\"((?:\\\\.|[^\"\\\\])*)\",\"monto\":(-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?),\"timestamp\":(-?\\d+)\\}"
        );

        Matcher matcher = patron.matcher(json);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "El JSON no tiene el formato esperado: " + json
            );
        }

        int id = Integer.parseInt(matcher.group(1));
        String origen = desescaparJson(matcher.group(2));
        double monto = Double.parseDouble(matcher.group(3));
        long timestamp = Long.parseLong(matcher.group(4));

        return new Transaccion(id, origen, monto, timestamp);
    }

    // Convierte una transacción a bytes binarios.
    public static byte[] transaccionABinario(Transaccion t)
            throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (DataOutputStream out = new DataOutputStream(buffer)) {
            byte[] origenBytes =
                    t.getOrigen().getBytes(StandardCharsets.UTF_8);

            out.writeInt(t.getIdTransaccion());
            out.writeInt(origenBytes.length);
            out.write(origenBytes);
            out.writeDouble(t.getMonto());
            out.writeLong(t.getTimestamp());
        }

        return buffer.toByteArray();
    }

    // Reconstruye una transacción desde bytes binarios.
    public static Transaccion binarioATransaccion(byte[] datos)
            throws IOException {

        try (DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(datos))) {

            int id = in.readInt();
            int longitudOrigen = in.readInt();

            if (longitudOrigen < 0 ||
                    longitudOrigen > in.available() - 16) {
                throw new IOException("Longitud de origen inválida");
            }

            byte[] origenBytes = new byte[longitudOrigen];
            in.readFully(origenBytes);

            String origen =
                    new String(origenBytes, StandardCharsets.UTF_8);

            double monto = in.readDouble();
            long timestamp = in.readLong();

            if (in.available() != 0) {
                throw new IOException("Hay bytes adicionales en el mensaje");
            }

            return new Transaccion(id, origen, monto, timestamp);
        }
    }

    private static String escaparJson(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String desescaparJson(String texto) {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            if (c != '\\') {
                resultado.append(c);
                continue;
            }

            if (++i >= texto.length()) {
                throw new IllegalArgumentException(
                        "Escape JSON incompleto"
                );
            }

            char escape = texto.charAt(i);

            switch (escape) {
                case '"': resultado.append('"'); break;
                case '\\': resultado.append('\\'); break;
                case '/': resultado.append('/'); break;
                case 'b': resultado.append('\b'); break;
                case 'f': resultado.append('\f'); break;
                case 'n': resultado.append('\n'); break;
                case 'r': resultado.append('\r'); break;
                case 't': resultado.append('\t'); break;
                case 'u':
                    if (i + 4 >= texto.length()) {
                        throw new IllegalArgumentException(
                                "Escape Unicode incompleto"
                        );
                    }
                    String hex = texto.substring(i + 1, i + 5);
                    resultado.append((char) Integer.parseInt(hex, 16));
                    i += 4;
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Escape JSON inválido: \\" + escape
                    );
            }
        }

        return resultado.toString();
    }
}
