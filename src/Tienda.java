/**
 * Fachada del dominio que integra el árbol de inventario, la cola prioritaria
 * de clientes, los carritos y el mapa de ubicaciones utilizado para las entregas.
 */
public class Tienda {

    private final ArbolProductos inventario;
    private final ColaClientes colaClientes;

    // Grafo utilizado para representar el mapa de entregas.
    private final Grafo grafo;

    // Ubicación fija de la tienda dentro del mapa.
    private final String ubicacion;

    /**
     * Crea una tienda con inventario y cola de clientes vacíos.
     * También crea el grafo y carga el mapa básico de ubicaciones.
     */
    public Tienda() {
        inventario = new ArbolProductos();
        colaClientes = new ColaClientes();

        grafo = new Grafo();
        grafo.cargarMapaBase();

        ubicacion = "Tienda Central";
    }

    /** Retorna el inventario basado en árbol binario de búsqueda. */
    public ArbolProductos getInventario() {
        return inventario;
    }

    /** Retorna la cola prioritaria de clientes pendiente de atención. */
    public ColaClientes getColaClientes() {
        return colaClientes;
    }

    /** Retorna la ubicación de la tienda dentro del mapa. */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Verifica si una ubicación ya está registrada en el grafo.
     *
     * @param ubicacion Ubicación que se desea buscar
     * @return true si existe, false en caso contrario
     */
    public boolean contieneUbicacion(String ubicacion) {
        return grafo.contieneVertice(ubicacion);
    }

    /**
     * Agrega una conexión entre dos ubicaciones existentes.
     *
     * @param origen Ubicación de origen
     * @param destino Ubicación de destino
     * @param distancia Distancia entre ambas ubicaciones
     * @return true si la conexión se agregó correctamente
     */
    public boolean agregarConexion(String origen, String destino, double distancia) {
        return grafo.agregarArista(origen, destino, distancia);
    }

    /**
     * Retorna las ubicaciones existentes dentro del mapa.
     *
     * @return Texto con las ubicaciones registradas
     */
    public String obtenerUbicaciones() {
        return grafo.obtenerUbicaciones();
    }

    /**
     * Agrega una nueva ubicación al mapa de entregas, independientemente
     * de si ya existe un cliente asociado a ella.
     *
     * @param ubicacion Nombre de la ubicación a registrar
     * @return true si se agregó correctamente, false si ya existía o era inválida
     */
    public boolean agregarUbicacion(String ubicacion) {
        return grafo.agregarVertice(ubicacion);
    }

    /**
     * Calcula la ruta más corta desde la tienda hasta cualquier
     * ubicación registrada en el mapa, sin necesidad de un cliente.
     *
     * @param destino Ubicación de destino
     * @return Resultado de la ruta calculada
     */
    public Ruta calcularRuta(String destino) {
        return grafo.dijkstra(ubicacion, destino);
    }

    /** Registra un producto en el inventario si el nombre no está repetido. */
    public boolean agregarProducto(Producto producto) {
        return inventario.insertar(producto);
    }

    /** Busca un producto disponible en el inventario por nombre. */
    public Producto buscarProducto(String nombre) {
        return inventario.buscar(nombre);
    }

    /**
     * Registra un cliente en la cola.
     * Si su ubicación todavía no existe en el grafo,
     * se agrega automáticamente como un nuevo vértice.
     *
     * @param cliente Cliente que se desea registrar
     */
    public void registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo.");
        }

        // La ubicación del cliente debe existir dentro del grafo.
        if (!grafo.contieneVertice(cliente.getUbicacion())) {
            grafo.agregarVertice(cliente.getUbicacion());
        }

        // Finalmente se agrega a la cola de prioridad.
        colaClientes.encolar(cliente);
    }

    /**
     * Añade unidades de un producto existente al carrito sin reservar stock.
     * La disponibilidad se valida ahora y se vuelve a validar al atender.
     */
    public boolean agregarAlCarrito(
            Cliente cliente,
            String nombreProducto,
            int cantidad) {

        if (cliente == null || cantidad <= 0) {
            return false;
        }

        Producto producto = buscarProducto(nombreProducto);

        if (producto == null) {
            return false;
        }

        int cantidadEnCarrito =
                cantidadEnCarrito(cliente, producto.getNombre());

        if (cantidadEnCarrito + cantidad > producto.getCantidad()) {
            return false;
        }

        return cliente.getCarrito()
                .agregarAlCarrito(producto, cantidad);
    }

    /**
     * Retorna la cantidad de un producto que el cliente
     * ya tiene agregada dentro de su carrito.
     */
    private int cantidadEnCarrito(
            Cliente cliente,
            String nombreProducto) {

        NodoProducto nodo =
                cliente.getCarrito().getPrimero();

        while (nodo != null) {

            if (nodo.getProducto()
                    .getNombre()
                    .equalsIgnoreCase(nombreProducto)) {

                return nodo.getProducto().getCantidad();
            }

            nodo = nodo.getSiguiente();
        }

        return 0;
    }

    /**
     * Calcula la ruta de entrega desde la ubicación de la tienda
     * hasta la ubicación del cliente utilizando Dijkstra.
     *
     * @param cliente Cliente al que se desea realizar la entrega
     * @return Resultado de la ruta calculada
     */
    public Ruta calcularRutaEntrega(Cliente cliente) {

        if (cliente == null) {
            return new Ruta(
                    "",
                    Double.POSITIVE_INFINITY,
                    false);
        }

        return grafo.dijkstra(
                ubicacion,
                cliente.getUbicacion());
    }

    /**
     * Verifica si existe una ruta entre la tienda
     * y la ubicación de un cliente.
     *
     * @param cliente Cliente que se desea verificar
     * @return true si existe una ruta de entrega
     */
    public boolean tieneRutaEntrega(Cliente cliente) {
        return calcularRutaEntrega(cliente)
                .esAlcanzable();
    }

    /**
     * Atiende al cliente de mayor prioridad.
     *
     * La operación solo se completa si existe una ruta
     * desde la tienda hasta el cliente y si hay suficiente
     * stock para todos los productos de su carrito.
     *
     * @return Cliente atendido o null si no puede atenderse
     */
    public Cliente atenderSiguiente() {

        Cliente cliente =
                colaClientes.verSiguiente();

        if (cliente == null ||
                cliente.getCarrito().estaVacia()) {

            return null;
        }

        // Antes de modificar el inventario se verifica
        // que exista una ruta de entrega.
        Ruta ruta =
                calcularRutaEntrega(cliente);

        if (!ruta.esAlcanzable()) {
            return null;
        }

        // Después se verifica que exista stock suficiente.
        if (!hayStockParaCompra(cliente)) {
            return null;
        }

        // Se descuentan del inventario los productos comprados.
        NodoProducto nodo =
                cliente.getCarrito().getPrimero();

        while (nodo != null) {

            Producto inventarioProducto =
                    buscarProducto(
                            nodo.getProducto().getNombre());

            inventarioProducto.setCantidad(
                    inventarioProducto.getCantidad()
                            - nodo.getProducto().getCantidad());

            nodo = nodo.getSiguiente();
        }

        // Solo después de completar correctamente la compra
        // el cliente se elimina de la cola.
        return colaClientes.desencolar();
    }

    /**
     * Verifica que exista suficiente stock para todos
     * los productos del carrito de un cliente.
     */
    private boolean hayStockParaCompra(Cliente cliente) {

        NodoProducto nodo =
                cliente.getCarrito().getPrimero();

        while (nodo != null) {

            Producto inventarioProducto =
                    buscarProducto(
                            nodo.getProducto().getNombre());

            if (inventarioProducto == null ||
                    nodo.getProducto().getCantidad()
                            > inventarioProducto.getCantidad()) {

                return false;
            }

            nodo = nodo.getSiguiente();
        }

        return true;
    }

    /**
     * Genera la factura del cliente atendido.
     * Además del detalle de la compra, se incluye
     * la ruta de entrega y la distancia total.
     *
     * @param cliente Cliente ya atendido
     * @return Texto completo de la factura
     */
    public String generarFactura(Cliente cliente) {

        if (cliente == null) {
            return "No hay cliente para facturar.";
        }

        Ruta ruta =
                calcularRutaEntrega(cliente);

        String informacionEntrega;

        if (ruta.esAlcanzable()) {

            informacionEntrega =
                    "\n=== INFORMACIÓN DE ENTREGA ===\n" +
                            "Ruta: " + ruta.getCamino() + "\n" +
                            String.format(
                                    "Distancia total: %.2f%n",
                                    ruta.getDistancia());

        } else {

            informacionEntrega =
                    "\n=== INFORMACIÓN DE ENTREGA ===\n" +
                            "No existe una ruta disponible para esta ubicación.\n";
        }

        return "=== FACTURA ===\n" +
                "Cliente: " + cliente.getNombre() +
                " | ID: " + cliente.getIdentificacion() + "\n" +
                "Tipo: " + cliente.getTipoCliente() + "\n" +
                "Ubicación: " + cliente.getUbicacion() + "\n\n" +

                cliente.getCarrito().representarCarrito() +

                String.format(
                        "\nTOTAL: ₡%.2f%n",
                        cliente.getCarrito().calcularTotal()) +

                informacionEntrega;
    }
}