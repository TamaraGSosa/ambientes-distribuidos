public final class Lectura {

    private final int sensorId;
    private final double temperatura;

    public Lectura(int sensorId, double temperatura) {
        this.sensorId = sensorId;
        this.temperatura = temperatura;
    }

    public int getSensorId() {
        return sensorId;
    }

    public double getTemperatura() {
        return temperatura;
    }
}