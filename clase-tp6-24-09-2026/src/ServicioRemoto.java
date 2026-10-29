
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioRemoto extends Remote {

    // Procesamiento matemático
    String calcularEstadisticas(double[] numeros) throws RemoteException;

    // Validación de datos
    boolean validarDNI(String dni) throws RemoteException;

    // Filtro de contenido
    String[] filtrarTextos(String[] textos, String patron) throws RemoteException;
}
