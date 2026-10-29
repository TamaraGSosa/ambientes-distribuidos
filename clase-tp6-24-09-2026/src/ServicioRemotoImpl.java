
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class ServicioRemotoImpl extends UnicastRemoteObject
        implements ServicioRemoto {

    public ServicioRemotoImpl() throws RemoteException {
        super();
    }

    @Override
    public String calcularEstadisticas(double[] numeros)
            throws RemoteException {

        System.out.println("Operación solicitada: calcular estadísticas.");

        if (numeros == null || numeros.length == 0) {
            return "Error: debe ingresar al menos un número.";
        }

        double suma = 0;
        double maximo = numeros[0];
        double minimo = numeros[0];

        for (double numero : numeros) {
            suma += numero;
            maximo = Math.max(maximo, numero);
            minimo = Math.min(minimo, numero);
        }

        double promedio = suma / numeros.length;
        double sumaCuadrados = 0;

        for (double numero : numeros) {
            sumaCuadrados += Math.pow(numero - promedio, 2);
        }

        double desviacion = Math.sqrt(sumaCuadrados / numeros.length);

        return String.format(
                "Promedio: %.2f | Máximo: %.2f | Mínimo: %.2f | Desviación estándar: %.2f",
                promedio, maximo, minimo, desviacion
        );
    }

    @Override
    public boolean validarDNI(String dni) throws RemoteException {

        System.out.println("Operación solicitada: validar DNI.");

        return dni != null && dni.matches("\\d{7,8}");
    }

    @Override
    public String[] filtrarTextos(String[] textos, String patron)
            throws RemoteException {

        System.out.println("Operación solicitada: filtrar textos.");

        if (textos == null || patron == null) {
            return new String[0];
        }

        return Arrays.stream(textos)
                .filter(texto -> texto != null
                        && texto.toLowerCase()
                                .contains(patron.toLowerCase()))
                .toArray(String[]::new);
    }
}
