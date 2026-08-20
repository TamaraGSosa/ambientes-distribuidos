import java.io.*;
import java.net.*;

public class Servidor {

    public static void main(String[] args) throws IOException {

        // Creamos el servidor en el puerto 5500
        ServerSocket server = new ServerSocket(5500);

        // Esperamos que se conecte un cliente
        Socket client = server.accept();

        // Recibimos información del cliente
        BufferedReader input =
                new BufferedReader(
                        new InputStreamReader(
                                client.getInputStream()));

        // Leemos el mensaje
        String message = input.readLine();

        // Mostramos lo que recibió el servidor
        System.out.println("Mensaje recibido: " + message);

        // Separamos los datos
        String[] datos = message.split(";");

        int numero1 = Integer.parseInt(datos[0]);
        String operacion = datos[1];
        int numero2 = Integer.parseInt(datos[2]);

        // Variable para guardar el resultado
        String resultado;

        // Realizamos la operación
        switch (operacion) {

            case "+":
                resultado = String.valueOf(numero1 + numero2);
                break;

            case "-":
                resultado = String.valueOf(numero1 - numero2);
                break;

            case "*":
                resultado = String.valueOf(numero1 * numero2);
                break;

            case "/":
                if (numero2 == 0) {
                    resultado = "ERROR: Division por cero";
                } else {
                    resultado = String.valueOf((double) numero1 / numero2);
                }
                break;

            default:
                resultado = "ERROR: Operacion no valida";
        }

        // Enviamos el resultado al cliente
        PrintWriter output =
                new PrintWriter(client.getOutputStream(), true);

        output.println(resultado);

        // Cerramos la conexión
        client.close();
        server.close();
    }
}