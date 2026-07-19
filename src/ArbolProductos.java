/**
 * Inventario implementado como árbol binario de búsqueda. La llave es el
 * nombre del producto, comparado sin distinguir mayúsculas de minúsculas.
 */
public class ArbolProductos {
    private NodoArbolProducto raiz;
    private int cantidadProductos;

    /** Indica si el árbol no contiene productos. */
    public boolean estaVacio() {
        return raiz == null;
    }

    /** Retorna el número de productos distintos registrados. */
    public int cantidadProductos() {
        return cantidadProductos;
    }

    /** Inserta un producto cuando su nombre todavía no existe en el árbol. */
    public boolean insertar(Producto producto) {
        if (producto == null || producto.getNombre() == null || producto.getNombre().isBlank()) {
            return false;
        }
        if (raiz == null) {
            raiz = new NodoArbolProducto(producto);
            cantidadProductos++;
            return true;
        }
        return insertar(raiz, producto);
    }

    private boolean insertar(NodoArbolProducto actual, Producto producto) {
        int comparacion = comparar(producto.getNombre(), actual.getProducto().getNombre());
        if (comparacion == 0) {
            return false;
        }
        if (comparacion < 0) {
            if (actual.getIzquierdo() == null) {
                actual.setIzquierdo(new NodoArbolProducto(producto));
                cantidadProductos++;
                return true;
            }
            return insertar(actual.getIzquierdo(), producto);
        }
        if (actual.getDerecho() == null) {
            actual.setDerecho(new NodoArbolProducto(producto));
            cantidadProductos++;
            return true;
        }
        return insertar(actual.getDerecho(), producto);
    }

    /** Busca un producto por nombre y retorna null cuando no existe. */
    public Producto buscar(String nombre) {
        NodoArbolProducto nodo = buscarNodo(nombre);
        return nodo == null ? null : nodo.getProducto();
    }

    /** Busca y retorna el nodo asociado al nombre solicitado. */
    public NodoArbolProducto buscarNodo(String nombre) {
        NodoArbolProducto actual = raiz;
        while (actual != null && nombre != null) {
            int comparacion = comparar(nombre, actual.getProducto().getNombre());
            if (comparacion == 0) {
                return actual;
            }
            actual = comparacion < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    /** Elimina un producto, incluyendo los casos de cero, uno o dos hijos. */
    public boolean eliminar(String nombre) {
        if (buscar(nombre) == null) {
            return false;
        }
        raiz = eliminar(raiz, nombre);
        cantidadProductos--;
        return true;
    }

    private NodoArbolProducto eliminar(NodoArbolProducto actual, String nombre) {
        if (actual == null) {
            return null;
        }
        int comparacion = comparar(nombre, actual.getProducto().getNombre());
        if (comparacion < 0) {
            actual.setIzquierdo(eliminar(actual.getIzquierdo(), nombre));
        } else if (comparacion > 0) {
            actual.setDerecho(eliminar(actual.getDerecho(), nombre));
        } else if (actual.getIzquierdo() == null) {
            return actual.getDerecho();
        } else if (actual.getDerecho() == null) {
            return actual.getIzquierdo();
        } else {
            NodoArbolProducto sucesor = minimo(actual.getDerecho());
            actual.setProducto(sucesor.getProducto());
            actual.setDerecho(eliminar(actual.getDerecho(), sucesor.getProducto().getNombre()));
        }
        return actual;
    }

    private NodoArbolProducto minimo(NodoArbolProducto nodo) {
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo;
    }

    /** Imprime el inventario en orden alfabético mediante recorrido in-order. */
    public void listarProductos() {
        if (estaVacio()) {
            System.out.println("No hay productos en el inventario.\n");
            return;
        }
        System.out.println("=== INVENTARIO (ORDEN ALFABÉTICO) ===");
        listarProductos(raiz);
        System.out.println("Total de productos: " + cantidadProductos + "\n");
    }

    private void listarProductos(NodoArbolProducto nodo) {
        if (nodo == null) return;
        listarProductos(nodo.getIzquierdo());
        System.out.println(nodo.getProducto());
        listarProductos(nodo.getDerecho());
    }

    private int comparar(String primero, String segundo) {
        return primero.trim().compareToIgnoreCase(segundo.trim());
    }
}
