
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        try {
            Registry registro = LocateRegistry.getRegistry("localhost", 1099);

            ServicioRemoto servicio =
                    (ServicioRemoto) registro.lookup("ServicioProcesamiento");

            System.out.println("Cliente conectado al servidor RMI.");

            Scanner scanner = new Scanner(System.in);
            int opcion;

            do {
                System.out.println("\n--- MENÚ DE PROCESAMIENTO REMOTO ---");
                System.out.println("1. Calcular estadísticas");
                System.out.println("2. Validar DNI");
                System.out.println("3. Filtrar textos");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");

                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese números separados por comas: ");
                        String[] datos = scanner.nextLine().split(",");

                        double[] numeros = new double[datos.length];

                        for (int i = 0; i < datos.length; i++) {
                            numeros[i] = Double.parseDouble(datos[i].trim());
                        }

                        System.out.println(servicio.calcularEstadisticas(numeros));
                        break;

                    case 2:
                        System.out.print("Ingrese el DNI: ");
                        String dni = scanner.nextLine();

                        System.out.println(
                                servicio.validarDNI(dni)
                                        ? "DNI con formato válido."
                                        : "DNI con formato inválido."
                        );
                        break;

                    case 3:
                        System.out.print("Ingrese textos separados por comas: ");
                        String[] textos = scanner.nextLine().split(",");

                        System.out.print("Ingrese el patrón de búsqueda: ");
                        String patron = scanner.nextLine();

                        String[] resultados = servicio.filtrarTextos(textos, patron);

                        System.out.println("Resultados encontrados:");
                        for (String texto : resultados) {
                            System.out.println("- " + texto.trim());
                        }
                        break;

                    case 0:
                        System.out.println("Cliente finalizado.");
                        break;

                    default:
                        System.out.println("Opción inválida.");
                }

            } while (opcion != 0);

            scanner.close();

        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }
}
