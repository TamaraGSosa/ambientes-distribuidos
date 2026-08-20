import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        // Pedimos los datos al usuario
        System.out.print("Ingrese el primer número: ");
        int numero1 = scanner.nextInt();

        System.out.print("Ingrese la operación (+, -, *, /): ");
        String operacion = scanner.next();

        System.out.print("Ingrese el segundo número: ");
        int numero2 = scanner.nextInt();

        // Nos conectamos al servidor
        Socket client = new Socket("localhost", 5500);

        // Permite enviar información al servidor
        PrintWriter output =
                new PrintWriter(client.getOutputStream(), true);

        // Armamos el mensaje
        String mensaje = numero1 + ";" + operacion + ";" + numero2;

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

        // Cerramos la conexión
        client.close();
        scanner.close();
    }
}