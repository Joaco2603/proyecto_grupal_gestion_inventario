/**
 * Lista enlazada simple para la gestión de productos.
 * Implementa inserción al inicio, al final, búsqueda, modificación,
 * eliminación y generación de reporte de costos.
 */
public class ListaProductos {

    private NodoProducto primero;

    /**
     * Crea una lista vacía.
     */
    public ListaProductos() {
        primero = null;
    }

    // Métodos de control

    /**
     * Verifica si la lista está vacía.
     * @return true si no hay elementos, false en caso contrario
     */
    public boolean estaVacia() {
        return primero == null;
    }

    public NodoProducto getPrimero() {
        return primero;
    }

    public void setPrimero(NodoProducto primero) {
        this.primero = primero;
    }

    // Inserción

    /**
     * Inserta un nuevo producto al inicio de la lista.
     * @param producto Producto a insertar
     */
    public void insertarAlInicio(Producto producto) {
        NodoProducto nuevoNodo = new NodoProducto(producto);
        nuevoNodo.setSiguiente(primero);
        primero = nuevoNodo;
    }

    /**
     * Inserta un nuevo producto al final de la lista.
     * @param producto Producto a insertar
     */
    public void insertarAlFinal(Producto producto) {
        NodoProducto nuevoNodo = new NodoProducto(producto);

        if (estaVacia()) {
            primero = nuevoNodo;
            return;
        }

        NodoProducto tmp = primero;
        while (tmp.getSiguiente() != null) {
            tmp = tmp.getSiguiente();
        }
        tmp.setSiguiente(nuevoNodo);
    }

    // Búsqueda

    /**
     * Busca un producto por su nombre (ignorando mayúsculas/minúsculas).
     * @param nombre Nombre del producto a buscar
     * @return El nodo que contiene el producto, o null si no se encuentra
     */
    public NodoProducto buscar(String nombre) {
        if (estaVacia()) {
            return null;
        }

        NodoProducto tmp = primero;
        while (tmp != null) {
            if (tmp.getProducto().getNombre().equalsIgnoreCase(nombre)) {
                return tmp;
            }
            tmp = tmp.getSiguiente();
        }
        return null;
    }

    // Modificación

    /**
     * Modifica los datos de un producto existente.
     * Busca por nombre original y reemplaza con los nuevos datos.
     *
     * @param nombreActual Nombre actual del producto a modificar
     * @param nuevosDatos  Objeto Producto con los datos actualizados
     * @return true si se modificó correctamente, false si no se encontró
     */
    public boolean modificarProducto(String nombreActual, Producto nuevosDatos) {
        NodoProducto nodo = buscar(nombreActual);
        if (nodo == null) {
            return false;
        }

        Producto p = nodo.getProducto();
        p.setNombre(nuevosDatos.getNombre());
        p.setPrecio(nuevosDatos.getPrecio());
        p.setCategoria(nuevosDatos.getCategoria());
        p.setFechaVencimiento(nuevosDatos.getFechaVencimiento());
        p.setCantidad(nuevosDatos.getCantidad());
        // NOTA: no se reemplaza la lista de imágenes — se maneja por separado
        return true;
    }

    /**
     * Agrega una ruta de imagen a la lista de imágenes de un producto.
     *
     * @param nombreProducto Nombre del producto
     * @param rutaImagen     Ruta de la imagen a agregar
     * @return true si se agregó correctamente, false si no se encontró el producto
     */
    public boolean agregarImagen(String nombreProducto, String rutaImagen) {
        NodoProducto nodo = buscar(nombreProducto);
        if (nodo == null || rutaImagen == null || rutaImagen.isBlank() || !rutaImagen.startsWith("images/")) {
            return false;
        }

        return nodo.getProducto().agregarImagen(rutaImagen.trim());
    }

    // Eliminación

    /**
     * Elimina un producto de la lista por su nombre.
     * @param nombre Nombre del producto a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     */
    public boolean eliminar(String nombre) {
        if (estaVacia()) {
            return false;
        }

        // Caso 1: el producto está al inicio
        if (primero.getProducto().getNombre().equalsIgnoreCase(nombre)) {
            primero = primero.getSiguiente();
            return true;
        }

        // Caso 2: el producto está en medio o al final
        NodoProducto tmp = primero;
        while (tmp.getSiguiente() != null) {
            if (tmp.getSiguiente().getProducto().getNombre().equalsIgnoreCase(nombre)) {
                tmp.setSiguiente(tmp.getSiguiente().getSiguiente());
                return true;
            }
            tmp = tmp.getSiguiente();
        }

        return false;
    }

    // Listado

    /**
     * Imprime todos los productos de la lista en consola.
     */
    public void listarProductos() {
        if (estaVacia()) {
            System.out.println("No hay productos registrados.\n");
            return;
        }

        System.out.println("=== LISTA DE PRODUCTOS ===");
        NodoProducto tmp = primero;
        int contador = 1;
        while (tmp != null) {
            System.out.println(contador + ". " + tmp.getProducto());
            tmp = tmp.getSiguiente();
            contador++;
        }
        System.out.println("Total de productos: " + (contador - 1) + "\n");
    }

    // Carrito de compras
    // (Estos métodos permiten que ListaProductos funcione como el carrito
    //  personal de un Cliente, además de seguir sirviendo como estructura
    //  de datos genérica de productos)

    /**
     * Agrega un producto al carrito a partir de un producto del inventario.
     * Nunca modifica el objeto original del inventario: crea una copia
     * independiente con la cantidad comprada, para no alterar accidentalmente
     * la existencia real de la tienda.
     * Si el producto ya está en el carrito, simplemente aumenta su cantidad.
     *
     * @param productoInventario Producto tal como está en el inventario (ArbolProductos)
     * @param cantidad           Cantidad que el cliente desea agregar al carrito
     * @return true si se agregó correctamente, false si los datos son inválidos
     */
    public boolean agregarAlCarrito(Producto productoInventario, int cantidad) {
        if (productoInventario == null || cantidad <= 0) {
            return false;
        }

        NodoProducto existente = buscar(productoInventario.getNombre());
        if (existente != null) {
            // Ya está en el carrito: se acumula la cantidad
            Producto productoCarrito = existente.getProducto();
            productoCarrito.setCantidad(productoCarrito.getCantidad() + cantidad);
            return true;
        }

        // No está en el carrito: se crea una copia independiente del producto,
        // NUNCA se reutiliza la referencia del inventario.
        Producto copia = new Producto(
                productoInventario.getNombre(),
                productoInventario.getPrecio(),
                productoInventario.getCategoria(),
                productoInventario.getFechaVencimiento(),
                cantidad
        );
        insertarAlFinal(copia);
        return true;
    }

    /**
     * Calcula el costo total acumulado de todos los productos del carrito
     * (suma de precio × cantidad de cada producto).
     * @return Costo total del carrito
     */
    public double calcularTotal() {
        double total = 0.0;
        NodoProducto tmp = primero;
        while (tmp != null) {
            total += tmp.getProducto().calcularCostoTotal();
            tmp = tmp.getSiguiente();
        }
        return total;
    }

    /**
     * Calcula la cantidad total de unidades (sumando cantidades de todos
     * los productos) que hay actualmente en el carrito.
     * @return Cantidad total de unidades en el carrito
     */
    public int calcularTotalItems() {
        int total = 0;
        NodoProducto tmp = primero;
        while (tmp != null) {
            total += tmp.getProducto().getCantidad();
            tmp = tmp.getSiguiente();
        }
        return total;
    }

    /**
     * Genera una representación en texto de los productos del carrito,
     * lista para incluirse dentro de una factura (una línea por producto,
     * con cantidad, precio unitario y subtotal).
     * No imprime nada directamente: retorna el texto para que quien la
     * invoque decida qué hacer con él (imprimirlo, agregarlo a un reporte, etc.).
     *
     * @return Texto formateado con el detalle del carrito, o un mensaje
     *         indicando que está vacío
     */
    public String representarCarrito() {
        if (estaVacia()) {
            return "El carrito está vacío.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %-10s %-12s %-12s%n",
                "Producto", "Cantidad", "Precio", "Subtotal"));

        NodoProducto tmp = primero;
        while (tmp != null) {
            Producto p = tmp.getProducto();
            sb.append(String.format("%-20s %-10d ₡%-11.2f ₡%-11.2f%n",
                    p.getNombre(), p.getCantidad(), p.getPrecio(), p.calcularCostoTotal()));
            tmp = tmp.getSiguiente();
        }

        return sb.toString();
    }

    // Reporte de costos

    /**
     * Recorre la lista e imprime un reporte con el costo total de cada producto
     * (precio × cantidad) y el costo total acumulado de toda la lista.
     */
    public void generarReporteCostos() {
        if (estaVacia()) {
            System.out.println("No hay productos para generar el reporte.\n");
            return;
        }

        System.out.println("REPORTE DE COSTOS");
        System.out.printf("\n %-20s %-10s %-10s %-12s%n", "Producto", "Precio", "Cantidad", "Costo Total \n");

        double costoAcumulado = 0.0;
        NodoProducto tmp = primero;

        while (tmp != null) {
            Producto p = tmp.getProducto();
            double costoTotal = p.calcularCostoTotal();
            costoAcumulado += costoTotal;

            System.out.printf("%-20s ₡%-9.2f %-10d ₡%-10.2f%n",
                    p.getNombre(), p.getPrecio(), p.getCantidad(), costoTotal);
            tmp = tmp.getSiguiente();
        }

        System.out.printf("\n %-42s ₡%-10.2f%n", "COSTO TOTAL ACUMULADO:", costoAcumulado);
        System.out.println();
    }
}