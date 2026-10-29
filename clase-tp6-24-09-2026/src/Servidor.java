
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {

    public static void main(String[] args) {
        try {
            ServicioRemoto servicio = new ServicioRemotoImpl();

            Registry registro = LocateRegistry.createRegistry(1099);
            registro.rebind("ServicioProcesamiento", servicio);

            System.out.println("Servidor RMI iniciado.");
            System.out.println("Servicio publicado correctamente.");
            System.out.println("Esperando solicitudes de clientes...");

        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}
