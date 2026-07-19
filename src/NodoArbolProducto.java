/**
 * Nodo de un árbol binario de búsqueda de productos. Conserva el producto
 * y las referencias a sus hijos izquierdo y derecho.
 */
public class NodoArbolProducto {
    private Producto producto;
    private NodoArbolProducto izquierdo;
    private NodoArbolProducto derecho;

    /** Crea un nodo hoja que almacena el producto indicado. */
    public NodoArbolProducto(Producto producto) {
        this.producto = producto;
    }

    /** Retorna el producto almacenado en este nodo. */
    public Producto getProducto() {
        return producto;
    }

    /** Reemplaza el producto almacenado, usado al eliminar un nodo con dos hijos. */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /** Retorna el hijo izquierdo. */
    public NodoArbolProducto getIzquierdo() {
        return izquierdo;
    }

    /** Asigna el hijo izquierdo. */
    public void setIzquierdo(NodoArbolProducto izquierdo) {
        this.izquierdo = izquierdo;
    }

    /** Retorna el hijo derecho. */
    public NodoArbolProducto getDerecho() {
        return derecho;
    }

    /** Asigna el hijo derecho. */
    public void setDerecho(NodoArbolProducto derecho) {
        this.derecho = derecho;
    }
}
