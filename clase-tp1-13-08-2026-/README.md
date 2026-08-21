# TP N.º 1 — Calculadora Distribuida con Sockets TCP
* `Alumna` : Gabriela Tamara Sosa

## Descripción

Este proyecto implementa una aplicación **Cliente-Servidor en Java** utilizando sockets TCP y la API `java.net`.

El sistema permite realizar operaciones matemáticas de forma remota. El cliente solicita dos números y una operación, envía los datos al servidor y recibe el resultado calculado.

##  Tecnologías utilizadas

* Java
* `java.net`
* `java.io`
* Sockets TCP

##  Estructura del proyecto

```text
clase-tp1-13-08-2026-/
├── Servidor.java
├── Cliente.java
└── README.md
```

## Funcionamiento

El cliente envía los datos al servidor utilizando el siguiente formato:

```text
numero1;operacion;numero2
```

Por ejemplo:

```text
15;+;30
```

El servidor recibe estos datos, realiza la operación correspondiente y devuelve el resultado al cliente.

### Ejemplo

```text
Cliente → 15;+;30
Servidor → 45
```

También se controla la división por cero:

```text
Cliente → 15;/;0
Servidor → ERROR: Division por cero
```

##  Ejecución

### 1. Iniciar el servidor

Primero se debe ejecutar `Servidor.java`.

El servidor queda esperando conexiones en el puerto:

```text
5500
```

### 2. Ejecutar el cliente

Con el servidor funcionando, ejecutar `Cliente.java`.

El cliente se conecta al servidor mediante:

```text
localhost:5500
```

Luego envía la operación y recibe el resultado.

##  Pruebas realizadas

### Suma

```text
15 + 30 = 45
```

### Resta

```text
30 - 15 = 15
```

### Multiplicación

```text
15 * 30 = 450
```

### División

```text
30 / 15 = 2
```

### División por cero

```text
15 / 0
```

Resultado:

```text
ERROR: Division por cero
```

##  Capturas de pantalla

### Servidor ejecutándose

![alt text](image.png)

### Cliente ejecutándose

![alt text](image-1.png)
### División por cero

![alt text](image-2.png)
## 📝 Ejercicio 2: Análisis Teórico-Práctico

### 1. ¿Qué sucede con el cliente si el servidor no está ejecutándose al momento de intentar conectar?

Si el servidor no está ejecutándose y el cliente intenta conectarse al puerto `5500`, Java lanza la excepción:

```text
java.net.ConnectException: Connection refused: connect
```

Esto sucede porque no hay ningún servidor escuchando en el puerto indicado.

La línea que genera la conexión es:

```java
Socket client = new Socket("localhost", 5500);
```

### 2. ¿Qué línea bloquea la ejecución del programa hasta que ocurre un evento de red?

En el servidor, la línea:

```java
Socket client = server.accept();
```

bloquea la ejecución hasta que un cliente se conecta.

El método `accept()` queda esperando una conexión entrante.

Además, la siguiente línea también puede bloquear la ejecución mientras espera recibir datos:

```java
String message = input.readLine();
```

En este caso, `readLine()` espera hasta recibir información del cliente.

### 3. ¿Qué cambios serían necesarios para ejecutar el Cliente y el Servidor en dos notebooks diferentes?

Si el Cliente y el Servidor están en dos notebooks conectadas a la misma red Wi-Fi, no se debe utilizar `localhost` en el Cliente, ya que `localhost` hace referencia a la propia computadora.

Actualmente se utiliza:

```java
Socket client = new Socket("localhost", 5500);
```

Se debería reemplazar `localhost` por la dirección IP de la notebook donde se ejecuta el servidor. Por ejemplo:

```java
Socket client = new Socket("192.168.1.25", 5500);
```

Además:

* Ambas notebooks deben estar conectadas a la misma red Wi-Fi.
* El servidor debe estar ejecutándose y escuchando en el puerto `5500`.
* El firewall de la notebook servidor debe permitir conexiones entrantes por el puerto `5500`.
* El Cliente debe utilizar la dirección IP de la notebook del Servidor.
* Ambas aplicaciones deben utilizar el mismo puerto.


##  Conclusión

La práctica permitió comprender el funcionamiento básico de una arquitectura Cliente-Servidor y la comunicación entre procesos mediante sockets TCP.

El cliente se encarga de solicitar y enviar los datos, mientras que el servidor recibe la información, realiza la operación matemática y devuelve el resultado.

También se pudo observar el comportamiento del sistema frente a errores, como la división por cero y la imposibilidad de conectarse cuando el servidor no está disponible.
