/** Arista enlazada que guarda un destino y su distancia. */
public class AristaGrafo {
    private VerticeGrafo destino;
    private double distancia;
    private AristaGrafo siguiente;

    public AristaGrafo(VerticeGrafo destino, double distancia) {
        this.destino = destino;
        this.distancia = distancia;
        siguiente = null;
    }

    public VerticeGrafo getDestino() {
        return destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public AristaGrafo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(AristaGrafo siguiente) {
        this.siguiente = siguiente;
    }
}
