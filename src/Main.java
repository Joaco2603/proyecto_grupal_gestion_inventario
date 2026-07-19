import java.util.Scanner;

/** Punto de entrada y menú de consola de la aplicación. */
public class Main {
    /** Muestra el menú principal y dirige cada operación a la tienda. */
    public static void menu(Tienda tienda, Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n=== GESTIÓN DE INVENTARIOS ===");
            System.out.println("1. Agregar producto al inventario");
            System.out.println("2. Buscar producto");
            System.out.println("3. Listar inventario");
            System.out.println("4. Registrar cliente y llenar carrito");
            System.out.println("5. Ver cola de clientes");
            System.out.println("6. Atender siguiente cliente");
            System.out.println("7. Salir");
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> agregarProducto(tienda, scanner);
                case 2 -> buscarProducto(tienda, scanner);
                case 3 -> tienda.getInventario().listarProductos();
                case 4 -> registrarCliente(tienda, scanner);
                case 5 -> tienda.getColaClientes().mostrarCola();
                case 6 -> atenderCliente(tienda);
                case 7 -> System.out.println("Hasta luego.");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 7);
    }

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

    private static void registrarCliente(Tienda tienda, Scanner scanner) {
        String identificacion = leerTexto(scanner, "Identificación: ");
        String nombre = leerTexto(scanner, "Nombre: ");
        int prioridad = leerPrioridad(scanner);

        Cliente cliente = new Cliente(identificacion, nombre, prioridad);
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
            System.out.println("No se puede atender: el stock actual no permite completar la compra.");
            return;
        }
        System.out.println(tienda.generarFactura(atendido));
    }

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
            System.out.println("El precio debe ser un número mayor que cero.");
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            menu(new Tienda(), scanner);
        }
    }
}
