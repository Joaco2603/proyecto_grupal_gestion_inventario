/**
 * Integra el inventario, la cola de clientes, los carritos y el mapa de
 * ubicaciones utilizado para las entregas.
 */
public class Tienda {
    private final ArbolProductos inventario;
    private final ColaClientes colaClientes;
    private final Grafo grafo;
    private final String ubicacion;

    /** Crea una tienda con inventario, cola y mapa base vacíos de clientes. */
    public Tienda() {
        inventario = new ArbolProductos();
        colaClientes = new ColaClientes();
        grafo = new Grafo();
        grafo.cargarMapaBase();
        ubicacion = "Tienda Central";
    }

    public ArbolProductos getInventario() {
        return inventario;
    }

    public ColaClientes getColaClientes() {
        return colaClientes;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    /** Verifica si una ubicación existe en el mapa. */
    public boolean contieneUbicacion(String nombreUbicacion) {
        return grafo.contieneVertice(nombreUbicacion);
    }

    /** Agrega una conexión entre dos ubicaciones existentes. */
    public boolean agregarConexion(String origen, String destino, double distancia) {
        return grafo.agregarArista(origen, destino, distancia);
    }

    /** Retorna las ubicaciones registradas, separadas por comas. */
    public String obtenerUbicaciones() {
        return grafo.obtenerUbicaciones();
    }

    /** Agrega una nueva ubicación al mapa. */
    public boolean agregarUbicacion(String nombreUbicacion) {
        return grafo.agregarVertice(nombreUbicacion);
    }

    /** Calcula la ruta más corta desde la tienda hasta una ubicación. */
    public Ruta calcularRuta(String destino) {
        return grafo.dijkstra(ubicacion, destino);
    }

    /** Agrega un producto al inventario. */
    public boolean agregarProducto(Producto producto) {
        return inventario.insertar(producto);
    }

    /** Busca un producto por nombre. */
    public Producto buscarProducto(String nombre) {
        return inventario.buscar(nombre);
    }

    /**
     * Registra un cliente y agrega automáticamente su ubicación al grafo si
     * todavía no existe.
     */
    public void registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }

        if (!grafo.contieneVertice(cliente.getUbicacion())) {
            grafo.agregarVertice(cliente.getUbicacion());
        }
        colaClientes.encolar(cliente);
    }

    /** Agrega unidades de un producto disponible al carrito del cliente. */
    public boolean agregarAlCarrito(Cliente cliente, String nombreProducto, int cantidad) {
        if (cliente == null || cantidad <= 0) {
            return false;
        }

        Producto producto = buscarProducto(nombreProducto);
        if (producto == null) {
            return false;
        }

        int cantidadActual = cantidadEnCarrito(cliente, producto.getNombre());
        if (cantidadActual + cantidad > producto.getCantidad()) {
            return false;
        }

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

    /** Calcula la ruta de entrega hasta la ubicación del cliente. */
    public Ruta calcularRutaEntrega(Cliente cliente) {
        if (cliente == null) {
            return new Ruta("", Double.POSITIVE_INFINITY, false);
        }
        return grafo.dijkstra(ubicacion, cliente.getUbicacion());
    }

    /** Indica si el cliente tiene una ruta de entrega disponible. */
    public boolean tieneRutaEntrega(Cliente cliente) {
        return calcularRutaEntrega(cliente).esAlcanzable();
    }

    /**
     * Atiende al cliente de mayor prioridad solo cuando su ruta existe y hay
     * suficiente inventario para completar todo el carrito.
     */
    public Cliente atenderSiguiente() {
        Cliente cliente = colaClientes.verSiguiente();
        if (cliente == null || cliente.getCarrito().estaVacia()) {
            return null;
        }

        if (!tieneRutaEntrega(cliente) || !hayStockParaCompra(cliente)) {
            return null;
        }

        NodoProducto nodo = cliente.getCarrito().getPrimero();
        while (nodo != null) {
            Producto producto = buscarProducto(nodo.getProducto().getNombre());
            producto.setCantidad(producto.getCantidad() - nodo.getProducto().getCantidad());
            nodo = nodo.getSiguiente();
        }

        return colaClientes.desencolar();
    }

    private boolean hayStockParaCompra(Cliente cliente) {
        NodoProducto nodo = cliente.getCarrito().getPrimero();
        while (nodo != null) {
            Producto producto = buscarProducto(nodo.getProducto().getNombre());
            if (producto == null || nodo.getProducto().getCantidad() > producto.getCantidad()) {
                return false;
            }
            nodo = nodo.getSiguiente();
        }
        return true;
    }

    /** Genera la factura con los datos de compra y entrega. */
    public String generarFactura(Cliente cliente) {
        if (cliente == null) {
            return "No hay cliente para facturar.";
        }

        Ruta ruta = calcularRutaEntrega(cliente);
        String entrega;
        if (ruta.esAlcanzable()) {
            entrega = "\n=== INFORMACIÓN DE ENTREGA ===\n" +
                    "Ruta: " + ruta.getCamino() + "\n" +
                    String.format("Distancia total: %.2f%n", ruta.getDistancia());
        } else {
            entrega = "\n=== INFORMACIÓN DE ENTREGA ===\n" +
                    "No existe una ruta disponible para esta ubicación.\n";
        }

        return "=== FACTURA ===\n" +
                "Cliente: " + cliente.getNombre() +
                " | ID: " + cliente.getIdentificacion() + "\n" +
                "Tipo: " + cliente.getTipoCliente() + "\n" +
                "Ubicación: " + cliente.getUbicacion() + "\n\n" +
                cliente.getCarrito().representarCarrito() +
                String.format("\nTOTAL: ₡%.2f%n", cliente.getCarrito().calcularTotal()) +
                entrega;
    }
}
