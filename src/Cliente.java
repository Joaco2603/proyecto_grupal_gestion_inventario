/**
 * Representa un cliente dentro del sistema de gestión de inventarios.
 * Cada cliente tiene una identificación, un nombre, una prioridad de
 * atención (1 a 3) y un carrito de compras propio e independiente.
 * La prioridad determina el orden de atención dentro de ColaClientes:
 *   1 = Básico
 *   2 = Afiliado
 *   3 = Premium
 */
public class Cliente {

    // Constantes de prioridad
    public static final int PRIORIDAD_BASICO = 1;
    public static final int PRIORIDAD_AFILIADO = 2;
    public static final int PRIORIDAD_PREMIUM = 3;

    // Atributos
    private String identificacion;
    private String nombre;
    private int prioridad;
    private ListaProductos carrito;

    // Constructor

    /**
     * Crea un cliente con su identificación, nombre y prioridad de atención.
     * El carrito se inicializa vacío automáticamente.
     *
     * @param identificacion Identificación única del cliente (cédula, código, etc.)
     * @param nombre         Nombre del cliente
     * @param prioridad      Prioridad de atención (1 = Básico, 2 = Afiliado, 3 = Premium)
     * @throws IllegalArgumentException si la prioridad no está entre 1 y 3
     */
    public Cliente(String identificacion, String nombre, int prioridad) {
        if (!validarPrioridad(prioridad)) {
            throw new IllegalArgumentException(
                    "La prioridad debe estar entre " + PRIORIDAD_BASICO +
                            " (Básico) y " + PRIORIDAD_PREMIUM + " (Premium).");
        }

        this.identificacion = identificacion;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.carrito = new ListaProductos(); // cada cliente tiene su propio carrito
    }

    // Validación

    /**
     * Verifica que una prioridad esté dentro del rango permitido (1 a 3).
     * @param prioridad Prioridad a validar
     * @return true si la prioridad es válida
     */
    public static boolean validarPrioridad(int prioridad) {
        return prioridad >= PRIORIDAD_BASICO && prioridad <= PRIORIDAD_PREMIUM;
    }

    // Getters
    // (No se agregan setters para identificacion/nombre/prioridad: un cliente
    //  no debería cambiar de identidad o prioridad una vez creado dentro del flujo)

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public ListaProductos getCarrito() {
        return carrito;
    }

    // Métodos adicionales

    /**
     * Traduce el valor numérico de la prioridad a su nombre descriptivo.
     * @return "Básico", "Afiliado" o "Premium" según corresponda
     */
    public String getTipoCliente() {
        switch (prioridad) {
            case PRIORIDAD_PREMIUM:
                return "Premium";
            case PRIORIDAD_AFILIADO:
                return "Afiliado";
            default:
                return "Básico";
        }
    }

    @Override
    public String toString() {
        return "Cliente: " + nombre +
                " | ID: " + identificacion +
                " | Tipo: " + getTipoCliente() +
                " | Productos en carrito: " + (carrito.estaVacia() ? 0 : carrito.calcularTotalItems());
    }
}