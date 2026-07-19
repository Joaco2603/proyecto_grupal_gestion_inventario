/**
 * Fachada del dominio que integra el árbol de inventario, la cola prioritaria
 * y los carritos de los clientes durante el proceso de compra.
 */
public class Tienda {
    private final ArbolProductos inventario;
    private final ColaClientes colaClientes;

    /** Crea una tienda con inventario y cola de clientes vacíos. */
    public Tienda() {
        inventario = new ArbolProductos();
        colaClientes = new ColaClientes();
    }

    /** Retorna el inventario basado en árbol binario de búsqueda. */
    public ArbolProductos getInventario() {
        return inventario;
    }

    /** Retorna la cola prioritaria de clientes pendiente de atención. */
    public ColaClientes getColaClientes() {
        return colaClientes;
    }

    /** Registra un producto en el inventario si el nombre no está repetido. */
    public boolean agregarProducto(Producto producto) {
        return inventario.insertar(producto);
    }

    /** Busca un producto disponible en el inventario por nombre. */
    public Producto buscarProducto(String nombre) {
        return inventario.buscar(nombre);
    }

    /** Encola un cliente después de llenar su carrito. */
    public void registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        colaClientes.encolar(cliente);
    }

    /**
     * Añade unidades de un producto existente al carrito sin reservar stock.
     * La disponibilidad se valida ahora y se vuelve a validar al atender.
     */
    public boolean agregarAlCarrito(Cliente cliente, String nombreProducto, int cantidad) {
        if (cliente == null || cantidad <= 0) return false;
        Producto producto = buscarProducto(nombreProducto);
        if (producto == null) return false;
        int cantidadEnCarrito = cantidadEnCarrito(cliente, producto.getNombre());
        if (cantidadEnCarrito + cantidad > producto.getCantidad()) return false;
        return cliente.getCarrito().agregarAlCarrito(producto, cantidad);
    }

    private int cantidadEnCarrito(Cliente cliente, String nombreProducto) {
        NodoProducto nodo = cliente.getCarrito().getPrimero();
        while (nodo != null) {
            if (nodo.getProducto().getNombre().equalsIgnoreCase(nombreProducto)) {
                return nodo.getProducto().getCantidad();
            }
            nodo = nodo.getSiguiente();
        }
        return 0;
    }

    /**
     * Atiende al cliente de mayor prioridad y descuenta su compra del inventario.
     * No modifica existencias si falta stock para al menos una línea del carrito.
     */
    public Cliente atenderSiguiente() {
        Cliente cliente = colaClientes.verSiguiente();
        if (cliente == null || cliente.getCarrito().estaVacia()) return null;
        if (!hayStockParaCompra(cliente)) return null;

        NodoProducto nodo = cliente.getCarrito().getPrimero();
        while (nodo != null) {
            Producto inventarioProducto = buscarProducto(nodo.getProducto().getNombre());
            inventarioProducto.setCantidad(inventarioProducto.getCantidad() - nodo.getProducto().getCantidad());
            nodo = nodo.getSiguiente();
        }
        return colaClientes.desencolar();
    }

    private boolean hayStockParaCompra(Cliente cliente) {
        NodoProducto nodo = cliente.getCarrito().getPrimero();
        while (nodo != null) {
            Producto inventarioProducto = buscarProducto(nodo.getProducto().getNombre());
            if (inventarioProducto == null || nodo.getProducto().getCantidad() > inventarioProducto.getCantidad()) {
                return false;
            }
            nodo = nodo.getSiguiente();
        }
        return true;
    }

    /** Genera el texto de factura del cliente ya atendido. */
    public String generarFactura(Cliente cliente) {
        if (cliente == null) return "No hay cliente para facturar.";
        return "=== FACTURA ===\n" +
                "Cliente: " + cliente.getNombre() + " | ID: " + cliente.getIdentificacion() + "\n" +
                "Tipo: " + cliente.getTipoCliente() + "\n\n" +
                cliente.getCarrito().representarCarrito() +
                String.format("\nTOTAL: ₡%.2f%n", cliente.getCarrito().calcularTotal());
    }
}
