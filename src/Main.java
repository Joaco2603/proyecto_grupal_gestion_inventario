import java.util.Scanner;

/** Punto de entrada y menú de consola de la aplicación. */
public class Main {

    /** Muestra el menú principal y dirige cada operación a la tienda. */
    public static void menu(Tienda tienda, Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n=== GESTIÓN DE INVENTARIOS ===");
            System.out.println("--- Productos ---");
            System.out.println("1. Agregar producto al inventario");
            System.out.println("2. Buscar producto");
            System.out.println("3. Listar inventario");
            System.out.println("4. Modificar producto");
            System.out.println("5. Eliminar producto");
            System.out.println("6. Agregar imagen a un producto");
            System.out.println("--- Clientes y compras ---");
            System.out.println("7. Registrar cliente y llenar carrito");
            System.out.println("8. Ver cola de clientes");
            System.out.println("9. Atender siguiente cliente");
            System.out.println("--- Mapa de entregas ---");
            System.out.println("10. Ver ubicaciones registradas");
            System.out.println("11. Agregar ubicación");
            System.out.println("12. Conectar dos ubicaciones");
            System.out.println("13. Calcular ruta hacia una ubicación");
            System.out.println("14. Salir");
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1:
                    agregarProducto(tienda, scanner);
                    break;
                case 2:
                    buscarProducto(tienda, scanner);
                    break;
                case 3:
                    tienda.getInventario().listarProductos();
                    break;
                case 4:
                    modificarProducto(tienda, scanner);
                    break;
                case 5:
                    eliminarProducto(tienda, scanner);
                    break;
                case 6:
                    agregarImagenProducto(tienda, scanner);
                    break;
                case 7:
                    registrarCliente(tienda, scanner);
                    break;
                case 8:
                    tienda.getColaClientes().mostrarCola();
                    break;
                case 9:
                    atenderCliente(tienda);
                    break;
                case 10:
                    mostrarUbicaciones(tienda);
                    break;
                case 11:
                    agregarUbicacion(tienda, scanner);
                    break;
                case 12:
                    conectarUbicaciones(tienda, scanner);
                    break;
                case 13:
                    calcularRuta(tienda, scanner);
                    break;
                case 14:
                    System.out.println("Hasta luego.");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 14);
    }

    // ---------------------------------------------------------------
    // Productos
    // ---------------------------------------------------------------

    private static void agregarProducto(Tienda tienda, Scanner scanner) {
        String nombre = leerTexto(scanner, "Nombre: ");
        double precio = leerDoublePositivo(scanner, "Precio: ");
        String categoria = leerTexto(scanner, "Categoría: ");
        String fecha = leerTextoOpcional(scanner, "Fecha de vencimiento (opcional): ");
        int cantidad = leerEnteroNoNegativo(scanner, "Cantidad: ");
        boolean agregado = tienda.agregarProducto(new Producto(nombre, precio, categoria, fecha, cantidad));
        System.out.println(agregado ? "Producto agregado." : "No se pudo agregar: nombre duplicado o inválido.");
    }

    private static void buscarProducto(Tienda tienda, Scanner scanner) {
        Producto producto = tienda.buscarProducto(leerTexto(scanner, "Nombre del producto: "));
        System.out.println(producto == null ? "Producto no encontrado." : producto);
    }

    /**
     * Modifica precio, categoría, fecha de vencimiento y cantidad de un
     * producto existente. El nombre no se puede modificar aquí porque es
     * la llave que ordena el árbol de inventario.
     */
    private static void modificarProducto(Tienda tienda, Scanner scanner) {
        String nombre = leerTexto(scanner, "Nombre del producto a modificar: ");
        Producto producto = tienda.buscarProducto(nombre);
        if (producto == null) {
            System.out.println("Producto no encontrado.");
            return;
        }
        System.out.println("Producto actual: " + producto);
        System.out.println("(el nombre no se puede modificar; cree uno nuevo si necesita renombrarlo)");
        producto.setPrecio(leerDoublePositivo(scanner, "Nuevo precio: "));
        producto.setCategoria(leerTexto(scanner, "Nueva categoría: "));
        producto.setFechaVencimiento(leerTextoOpcional(scanner, "Nueva fecha de vencimiento (opcional): "));
        producto.setCantidad(leerEnteroNoNegativo(scanner, "Nueva cantidad: "));
        System.out.println("Producto actualizado: " + producto);
    }

    private static void eliminarProducto(Tienda tienda, Scanner scanner) {
        String nombre = leerTexto(scanner, "Nombre del producto a eliminar: ");
        boolean eliminado = tienda.getInventario().eliminar(nombre);
        System.out.println(eliminado ? "Producto eliminado." : "Producto no encontrado.");
    }

    private static void agregarImagenProducto(Tienda tienda, Scanner scanner) {
        String nombre = leerTexto(scanner, "Nombre del producto: ");
        Producto producto = tienda.buscarProducto(nombre);
        if (producto == null) {
            System.out.println("Producto no encontrado.");
            return;
        }
        String ruta = leerTexto(scanner, "Ruta de la imagen (debe iniciar con 'images/'): ");
        boolean agregada = producto.agregarImagen(ruta);
        System.out.println(agregada ? "Imagen agregada." : "No se pudo agregar la imagen.");
    }

    // ---------------------------------------------------------------
    // Clientes y compras
    // ---------------------------------------------------------------

    private static void registrarCliente(Tienda tienda, Scanner scanner) {
        String identificacion = leerTexto(scanner, "Identificación: ");
        String nombre = leerTexto(scanner, "Nombre: ");
        int prioridad = leerPrioridad(scanner);

        String ubicaciones = tienda.obtenerUbicaciones();
        if (!ubicaciones.isEmpty()) {
            System.out.println("Ubicaciones registradas: " + ubicaciones);
        }
        String ubicacion = leerTexto(scanner, "Ubicación del cliente (para calcular la entrega): ");

        Cliente cliente = new Cliente(identificacion, nombre, prioridad, ubicacion);
        while (true) {
            String producto = leerTextoOpcional(scanner, "Producto para el carrito (ENTER para terminar): ");
            if (producto == null || producto.isEmpty()) break;
            int cantidad = leerEnteroPositivo(scanner, "Cantidad: ");
            if (tienda.agregarAlCarrito(cliente, producto, cantidad)) {
                System.out.println("Producto agregado al carrito.");
            } else {
                System.out.println("No se pudo agregar: producto inexistente o stock insuficiente.");
            }
        }
        if (cliente.getCarrito().estaVacia()) {
            System.out.println("El cliente no tiene productos; no se agregó a la cola.");
            return;
        }
        tienda.registrarCliente(cliente);
        System.out.println("Cliente agregado a la cola.");
    }

    private static void atenderCliente(Tienda tienda) {
        Cliente siguiente = tienda.getColaClientes().verSiguiente();
        if (siguiente == null) {
            System.out.println("No hay clientes en la cola.");
            return;
        }
        Cliente atendido = tienda.atenderSiguiente();
        if (atendido == null) {
            System.out.println("No se puede atender: falta stock suficiente o no existe ruta de entrega hasta "
                    + siguiente.getUbicacion() + ".");
            return;
        }
        System.out.println(tienda.generarFactura(atendido));
    }

    // ---------------------------------------------------------------
    // Mapa de entregas
    // ---------------------------------------------------------------

    private static void mostrarUbicaciones(Tienda tienda) {
        String ubicaciones = tienda.obtenerUbicaciones();
        System.out.println(ubicaciones.isEmpty() ? "No hay ubicaciones registradas." : ubicaciones);
    }

    private static void agregarUbicacion(Tienda tienda, Scanner scanner) {
        String ubicacion = leerTexto(scanner, "Nombre de la nueva ubicación: ");
        boolean agregada = tienda.agregarUbicacion(ubicacion);
        System.out.println(agregada ? "Ubicación agregada." : "Ya existe o el nombre no es válido.");
    }

    private static void conectarUbicaciones(Tienda tienda, Scanner scanner) {
        System.out.println("Ubicaciones registradas: " + tienda.obtenerUbicaciones());
        String origen = leerTexto(scanner, "Ubicación de origen: ");
        String destino = leerTexto(scanner, "Ubicación de destino: ");
        double distancia = leerDoublePositivo(scanner, "Distancia: ");
        boolean conectada = tienda.agregarConexion(origen, destino, distancia);
        System.out.println(conectada
                ? "Conexión agregada."
                : "No se pudo conectar: verifique que ambas ubicaciones existan, sean distintas y la distancia sea mayor a cero.");
    }

    private static void calcularRuta(Tienda tienda, Scanner scanner) {
        System.out.println("Ubicaciones registradas: " + tienda.obtenerUbicaciones());
        String destino = leerTexto(scanner, "Ubicación de destino: ");
        Ruta ruta = tienda.calcularRuta(destino);
        System.out.println(ruta);
    }

    // ---------------------------------------------------------------
    // Utilidades de lectura por consola
    // ---------------------------------------------------------------

    private static String leerTexto(Scanner scanner, String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = scanner.nextLine().trim();
        } while (texto.isEmpty());
        return texto;
    }

    private static String leerTextoOpcional(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? null : texto;
    }

    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private static int leerEnteroNoNegativo(Scanner scanner, String mensaje) {
        int valor;
        do {
            valor = leerEntero(scanner, mensaje);
            if (valor < 0) System.out.println("La cantidad no puede ser negativa.");
        } while (valor < 0);
        return valor;
    }

    /** Lee una cantidad estrictamente positiva para una línea de compra. */
    private static int leerEnteroPositivo(Scanner scanner, String mensaje) {
        int valor;
        do {
            valor = leerEntero(scanner, mensaje);
            if (valor <= 0) System.out.println("La cantidad debe ser mayor que cero.");
        } while (valor <= 0);
        return valor;
    }

    /** Lee una prioridad válida y comunica claramente el rango permitido. */
    private static int leerPrioridad(Scanner scanner) {
        int prioridad;
        do {
            prioridad = leerEntero(scanner, "Prioridad (1 básico, 2 afiliado, 3 premium): ");
            if (!Cliente.validarPrioridad(prioridad)) {
                System.out.println("La prioridad debe ser 1, 2 o 3.");
            }
        } while (!Cliente.validarPrioridad(prioridad));
        return prioridad;
    }

    private static double leerDoublePositivo(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double valor = Double.parseDouble(scanner.nextLine().trim());
                if (valor > 0 && Double.isFinite(valor)) return valor;
            } catch (NumberFormatException ignored) {
                // Se muestra el mensaje común de validación.
            }
            System.out.println("El valor debe ser un número mayor que cero.");
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            menu(new Tienda(), scanner);
        }
    }
}
