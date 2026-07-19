/**
 * Nodo para la cola enlazada ColaClientes.
 * Cada nodo almacena un objeto Cliente y una referencia al siguiente nodo.
 */
public class NodoCliente {

    // Atributos
    private Cliente cliente;
    private NodoCliente siguiente;

    // Constructor

    /**
     * Crea un nodo con el cliente dado.
     * El puntero siguiente se inicializa en null.
     *
     * @param cliente Cliente que almacena este nodo
     */
    public NodoCliente(Cliente cliente) {
        this.cliente = cliente;
        this.siguiente = null;
    }

    // Getters y Setters

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public NodoCliente getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoCliente siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return cliente.toString();
    }
}