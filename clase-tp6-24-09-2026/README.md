# TP6 — Middleware y Objetos Remotos (Java RMI)

**Materia:** Desarrollo de Aplicaciones para Ambientes Distribuidos  
**Alumna:** Gabriela Tamara Sosa

## 1. Objetivo

Implementar un sistema distribuido en Java utilizando Java RMI para ejecutar operaciones de procesamiento remoto mediante un cliente y un servidor.

## 2. Estructura del proyecto

```text
TP6/
├── src/
│   ├── ServicioRemoto.java
│   ├── ServicioRemotoImpl.java
│   ├── Servidor.java
│   └── Cliente.java
├── evidencias/
│   ├── servidor.png
│   └── cliente.png
└── README.md
```

## 3. Funcionalidades implementadas

- **Procesamiento matemático:** calcula promedio, máximo, mínimo y desviación estándar de un conjunto de números.
- **Validación de DNI:** verifica que el DNI tenga 7 u 8 dígitos.
- **Filtro de textos:** devuelve los textos que contienen el patrón de búsqueda, sin distinguir mayúsculas y minúsculas.

## 4. Componentes del sistema

- **ServicioRemoto.java:** define la interfaz remota y declara los métodos disponibles.
- **ServicioRemotoImpl.java:** implementa la lógica de las operaciones remotas.
- **Servidor.java:** inicia el registro RMI en el puerto 1099 y publica el servicio con el nombre `ServicioProcesamiento`.
- **Cliente.java:** permite seleccionar y ejecutar las operaciones mediante un menú interactivo.

## 5. Compilación y ejecución

### 5.1. Ejecutar el servidor

```bash
Servidor.java
```
### 5.2. Ejecutar el cliente

Abrir una segunda terminal en la carpeta `TP6` y ejecutar:

```bash
Cliente.java
```

El cliente muestra un menú con las siguientes opciones:

1. Calcular estadísticas.
2. Validar DNI.
3. Filtrar textos.
0. Salir.

## 6. Evidencias

### Ejecución del servidor

![Ejecución del servidor](evidencias/servidor.png)

### Ejecución del cliente

![Ejecución del cliente](evidencias/cliente.png)


# 7. Cuestionario teórico y análisis de arquitectura

## 1. Abstracción del Middleware

 Explica la diferencia entre realizar una llamada remota mediante sockets puros versus hacerlo mediante Java RMI / gRPC. ¿Qué tareas complejas abstrae el middleware al desarrollador?

Con sockets puros, el desarrollador debe gestionar manualmente el envío y la recepción de datos, los protocolos de comunicación y el empaquetado de la información. En cambio, Java RMI y gRPC permiten invocar servicios remotos mediante abstracciones de alto nivel. El middleware se encarga de la serialización y deserialización de datos, la comunicación por red y la localización del servicio, simplificando el desarrollo de aplicaciones distribuidas.

## 2. Ciclo de Vida y Stub/Skeleton

 Describe qué es un Stub (o Proxy) en el lado del cliente y qué función cumple el Skeleton / Dispatcher en el lado del servidor durante la marshalling y unmarshalling de datos.



El **Stub o Proxy** es el intermediario del lado del cliente que representa al servicio remoto. Recibe la invocación, empaqueta los parámetros mediante *marshalling* y envía la solicitud al servidor.

El **Skeleton o Dispatcher** recibe la solicitud, desempaqueta los parámetros mediante *unmarshalling* y dirige la ejecución al método correspondiente. Luego, el resultado o la excepción se prepara para enviarse al cliente. En Java RMI moderno, estas tareas son gestionadas automáticamente por el sistema de ejecución de RMI.

## 3. Manejo de Fallos Parciales

¿Qué ocurre con el cliente si el servidor remoto se interrumpe abruptamente durante la ejecución de un método? Identifica las excepciones específicas que se generan e indica cómo deben gestionarse en un entorno productivo.

Si el servidor se interrumpe durante una invocación remota, el cliente puede recibir una RemoteException o alguna de sus subclases, como ConnectException o ConnectIOException, dependiendo del tipo de fallo.
En un entorno productivo, se deben capturar las excepciones, registrar el error e informar al usuario. También pueden implementarse reintentos controlados cuando sean seguros, tiempos de espera y mecanismos de recuperación. No se debe asumir que la operación no se ejecutó solo porque no llegó la respuesta, ya que el servidor podría haber completado el procesamiento antes de producirse el fallo de comunicación.

## 8. Conclusión

Se implementó un sistema de procesamiento remoto mediante Java RMI, separando la interfaz del servicio, su implementación, el servidor y el cliente. Las pruebas realizadas permitieron comprobar el funcionamiento de las tres operaciones y la comunicación entre ambos componentes.