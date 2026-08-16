/** Resultado sencillo del cálculo de una ruta. */
public class Ruta {
    private String camino;
    private double distancia;
    private boolean alcanzable;

    public Ruta(String camino, double distancia, boolean alcanzable) {
        this.camino = camino;
        this.distancia = distancia;
        this.alcanzable = alcanzable;
    }

    public String getCamino() {
        return camino;
    }

    public double getDistancia() {
        return distancia;
    }

    public boolean esAlcanzable() {
        return alcanzable;
    }

    @Override
    public String toString() {
        if (!alcanzable) {
            return "No existe una ruta entre las ubicaciones.";
        }
        return "Ruta: " + camino + " | Distancia: " + distancia;
    }
}
