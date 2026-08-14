import java.io //--->lectura y escritura de datos
import java.net //comunicacion de red
public class Servidor{
    public static void main(String[] args) {
        //ServerSocket clase para contruir un servidor despues le pongo el puerto
        SeverSocket server = new ServerSocket (5500);
        //ponemos para que se conecte con socket la clase java

        Socket client = server.accept()

        client.getInputStream() // recibir informacion de cliente
        // BufferedRead facilita leerlo
        BufferedReader input =
        new BufferedReader(
            // convwetimos los datos
                new InputStreamReader( 
                    //obtener los datos
                        client.getInputStream()));
        //lo guardamos en el servidor
        String message = input.readLine()
    }
}