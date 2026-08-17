public class InventoryTest {
    public static void main(String[] args) {
        testArbol();
        testPrioridad();
        testCompra();
        testCasosVacios();
        testGrafoYDijkstra();
        testTiendaEntrega();
        System.out.println("All inventory tests passed.");
    }

    private static void testArbol() {
        ArbolProductos arbol = new ArbolProductos();
        assertTrue(arbol.insertar(producto("Mango", 500, 4)), "insert mango");
        assertTrue(arbol.insertar(producto("Arroz", 900, 3)), "insert arroz");
        assertTrue(arbol.insertar(producto("Zanahoria", 300, 2)), "insert zanahoria");
        assertFalse(arbol.insertar(producto("arroz", 900, 1)), "reject duplicate");
        assertEquals("Arroz", arbol.buscar("ARROZ").getNombre(), "case-insensitive search");
        assertTrue(arbol.eliminar("Mango"), "delete root with two children");
        assertEquals(2, arbol.cantidadProductos(), "tree size");
        assertFalse(arbol.eliminar("No existe"), "cannot delete absent product");
    }

    private static void testPrioridad() {
        ColaClientes cola = new ColaClientes();
        Cliente basico = new Cliente("1", "Basic", 1, "Tienda Central");
        Cliente premium1 = new Cliente("2", "Premium 1", 3, "Tienda Central");
        Cliente premium2 = new Cliente("3", "Premium 2", 3, "Tienda Central");
        Cliente afiliado = new Cliente("4", "Affiliated", 2, "Tienda Central");
        cola.encolar(basico);
        cola.encolar(premium1);
        cola.encolar(afiliado);
        cola.encolar(premium2);
        assertSame(premium1, cola.desencolar(), "higher priority first");
        assertSame(premium2, cola.desencolar(), "FIFO priority tie");
        assertSame(afiliado, cola.desencolar(), "middle priority after premium");
        assertSame(basico, cola.desencolar(), "lower priority last");
        assertSame(null, cola.desencolar(), "empty queue returns null");
    }

    private static void testCompra() {
        Tienda tienda = new Tienda();
        tienda.agregarProducto(producto("Cafe", 1200, 5));
        // "Centro de Distribución" ya viene conectado a "Tienda Central" en el mapa base.
        Cliente cliente = new Cliente("10", "Client", 2, "Centro de Distribución");
        assertTrue(tienda.agregarAlCarrito(cliente, "cafe", 2), "add to cart");
        assertFalse(tienda.agregarAlCarrito(cliente, "cafe", 4), "reject insufficient stock");
        tienda.registrarCliente(cliente);
        assertSame(cliente, tienda.atenderSiguiente(), "attend client");
        assertEquals(3, tienda.buscarProducto("Cafe").getCantidad(), "decrement stock");
        assertEquals(2400.0, cliente.getCarrito().calcularTotal(), "invoice total");
    }

    private static void testCasosVacios() {
        Tienda tienda = new Tienda();
        assertTrue(tienda.getInventario().estaVacio(), "empty inventory");
        assertSame(null, tienda.atenderSiguiente(), "cannot attend empty queue");
        Cliente cliente = new Cliente("20", "Empty cart", 1, "Barrio Norte");
        tienda.registrarCliente(cliente);
        assertSame(null, tienda.atenderSiguiente(), "cannot attend empty cart");
    }

    private static void testGrafoYDijkstra() {
        Grafo grafo = new Grafo();
        assertTrue(grafo.agregarVertice("Tienda"), "add store vertex");
        assertTrue(grafo.agregarVertice("Cliente"), "add client vertex");
        assertTrue(grafo.agregarVertice("Centro"), "add intermediate vertex");
        assertTrue(grafo.agregarVertice("Bodega"), "add second intermediate vertex");
        assertFalse(grafo.agregarVertice(" tienda "), "reject duplicate vertex");

        assertTrue(grafo.agregarArista("Tienda", "Centro", 10), "add first edge");
        assertTrue(grafo.agregarArista("Centro", "Cliente", 5), "add second edge");
        assertTrue(grafo.agregarArista("Tienda", "Bodega", 3), "add alternate edge");
        assertTrue(grafo.agregarArista("Bodega", "Cliente", 20), "add longer edge");
        assertEquals(10.0, grafo.obtenerDistancia("Centro", "Tienda"), "edge is bidirectional");
        assertFalse(grafo.agregarArista("Centro", "Tienda", 10), "reject duplicate edge");
        assertFalse(grafo.agregarArista("Tienda", "Desconocido", 1), "reject unknown endpoint");
        assertFalse(grafo.agregarArista("Tienda", "Centro", -1), "reject negative distance");

        Ruta ruta = grafo.dijkstra("Cliente", "Tienda");
        assertEquals("Cliente -> Centro -> Tienda", ruta.getCamino(),
                "dijkstra path is undirected and shortest");
        assertEquals(15.0, ruta.getDistancia(), "dijkstra distance");

        Ruta desconectada = grafo.dijkstra("Tienda", "Aislado");
        assertFalse(desconectada.esAlcanzable(), "unknown destination is unreachable");
        assertTrue(Double.isInfinite(desconectada.getDistancia()), "unreachable distance is infinite");

        Ruta mismaUbicacion = grafo.dijkstra("Tienda", " tienda ");
        assertEquals("Tienda", mismaUbicacion.getCamino(), "same vertex path");
        assertEquals(0.0, mismaUbicacion.getDistancia(), "same vertex distance");

        Grafo mapaBase = new Grafo();
        mapaBase.cargarMapaBase();
        assertEquals(5, mapaBase.cantidadVertices(), "base map vertices");
        assertTrue(mapaBase.dijkstra("Zona Este", "Tienda Central").esAlcanzable(),
                "base map has connected route");
    }

    /**
     * Prueba de integración: producto + cliente + cola + grafo de entregas,
     * tal como se usa realmente desde el menú de Main.
     */
    private static void testTiendaEntrega() {
        Tienda tienda = new Tienda();
        tienda.agregarProducto(producto("Te", 800, 10));

        // Caso alcanzable: "Zona Este" ya está conectada en el mapa base
        // (Tienda Central -> Centro de Distribución -> Barrio Sur -> Zona Este).
        Cliente cliente = new Cliente("30", "Con ruta", 1, "Zona Este");
        assertTrue(tienda.agregarAlCarrito(cliente, "Te", 3), "add to cart for delivery test");
        tienda.registrarCliente(cliente);
        assertSame(cliente, tienda.atenderSiguiente(), "attend client with reachable route");
        String factura = tienda.generarFactura(cliente);
        assertTrue(factura.contains("Zona Este"), "invoice mentions delivery route");
        assertEquals(7, tienda.buscarProducto("Te").getCantidad(), "stock decremented after delivery");

        // Caso no alcanzable: ubicación registrada pero sin ninguna conexión.
        assertTrue(tienda.agregarUbicacion("Isla Aislada"), "add isolated location");
        Cliente aislado = new Cliente("31", "Sin ruta", 1, "Isla Aislada");
        assertTrue(tienda.agregarAlCarrito(aislado, "Te", 1), "add to cart for unreachable test");
        tienda.registrarCliente(aislado);
        assertSame(null, tienda.atenderSiguiente(), "cannot attend client without delivery route");

        // Conectar la ubicación aislada y volver a intentar.
        assertTrue(tienda.agregarConexion("Tienda Central", "Isla Aislada", 12), "connect isolated location");
        assertTrue(tienda.calcularRuta("Isla Aislada").esAlcanzable(), "route now reachable after connecting");
        assertSame(aislado, tienda.atenderSiguiente(), "attend client after route is connected");
    }

    private static Producto producto(String nombre, double precio, int cantidad) {
        return new Producto(nombre, precio, "General", null, cantidad);
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static void assertFalse(boolean value, String message) {
        assertTrue(!value, message);
    }

    private static void assertSame(Object expected, Object actual, String message) {
        if (expected != actual) throw new AssertionError(message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) throw new AssertionError(message + " expected=" + expected + " actual=" + actual);
    }
}