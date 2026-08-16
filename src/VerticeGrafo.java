/** Vértice de la lista enlazada que representa el grafo. */
public class VerticeGrafo {
    private String ubicacion;
    private AristaGrafo primeraArista;
    private VerticeGrafo siguiente;

    // Datos temporales usados directamente por Dijkstra.
    private boolean visitado;
    private double distanciaTemporal;
    private VerticeGrafo anterior;

    public VerticeGrafo(String ubicacion) {
        this.ubicacion = ubicacion;
        primeraArista = null;
        siguiente = null;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public AristaGrafo getPrimeraArista() {
        return primeraArista;
    }

    public void setPrimeraArista(AristaGrafo primeraArista) {
        this.primeraArista = primeraArista;
    }

    public VerticeGrafo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(VerticeGrafo siguiente) {
        this.siguiente = siguiente;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

    public double getDistanciaTemporal() {
        return distanciaTemporal;
    }

    public void setDistanciaTemporal(double distanciaTemporal) {
        this.distanciaTemporal = distanciaTemporal;
    }

    public VerticeGrafo getAnterior() {
        return anterior;
    }

    public void setAnterior(VerticeGrafo anterior) {
        this.anterior = anterior;
    }
}
