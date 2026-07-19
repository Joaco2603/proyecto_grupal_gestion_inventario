public class InventoryTest {
    public static void main(String[] args) {
        testArbol();
        testPrioridad();
        testCompra();
        testCasosVacios();
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
