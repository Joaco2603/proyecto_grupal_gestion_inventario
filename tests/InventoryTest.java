public class InventoryTest {
    public static void main(String[] args) {
        testArbol();
        testPrioridad();
        testCompra();
        testCasosVacios();
        testGrafoYDijkstra();
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
        Cliente basico = new Cliente("1", "Basic", 1);
        Cliente premium1 = new Cliente("2", "Premium 1", 3);
        Cliente premium2 = new Cliente("3", "Premium 2", 3);
        Cliente afiliado = new Cliente("4", "Affiliated", 2);
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
        Cliente cliente = new Cliente("10", "Client", 2);
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
        Cliente cliente = new Cliente("20", "Empty cart", 1);
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
