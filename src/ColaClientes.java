/**
 * Cola de prioridad para la atención de clientes.
 * Los clientes con mayor prioridad (Premium = 3) se atienden antes que
 * los de menor prioridad (Afiliado = 2, Básico = 1). Cuando dos o más
 * clientes tienen la misma prioridad, se respeta el orden de llegada
 * (cola estable: "primero en llegar, primero en salir" dentro de cada
 * nivel de prioridad).
 * Se implementa como una lista enlazada simple ordenada, en la que cada
 * cliente se inserta directamente en la posición que le corresponde según
 * su prioridad, en lugar de insertarse siempre al final.
 */
public class ColaClientes {

    private NodoCliente frente;
    private NodoCliente ultimo;
    private int cantidadClientes;

    /**
     * Crea una cola de clientes vacía.
     */
    public ColaClientes() {
        this.frente = null;
        this.ultimo = null;
        this.cantidadClientes = 0;
    }

    // Métodos de control

    /**
     * Verifica si la cola está vacía.
     * @return true si no hay clientes en la cola
     */
    public boolean estaVacia() {
        return frente == null;
    }

    /**
     * Retorna la cantidad de clientes actualmente en la cola.
     * @return Cantidad de clientes
     */
    public int cantidadClientes() {
        return cantidadClientes;
    }

    // Inserción (encolado por prioridad)

    /**
     * Inserta un cliente en la cola respetando su prioridad.
     * El cliente se coloca después de todos los clientes con prioridad
     * mayor o igual a la suya, y antes del primer cliente con prioridad
     * estrictamente menor. Esto garantiza que, entre clientes de la misma
     * prioridad, se mantenga el orden de llegada.
     *
     * @param cliente Cliente a encolar
     */
    public void encolar(Cliente cliente) {
        if (cliente == null) {
            return;
        }

        NodoCliente nuevoNodo = new NodoCliente(cliente);

        // Caso 1: cola vacía
        if (estaVacia()) {
            frente = nuevoNodo;
            ultimo = nuevoNodo;
            cantidadClientes++;
            return;
        }

        // Caso 1: el nuevo cliente tiene más prioridad que el que está al frente
        if (cliente.getPrioridad() > frente.getCliente().getPrioridad()) {
            nuevoNodo.setSiguiente(frente);
            frente = nuevoNodo;
            cantidadClientes++;
            return;
        }

        // Cso 3: se busca la posición correcta avanzando mientras el
        // siguiente nodo tenga prioridad mayor o igual a la del nuevo cliente
        NodoCliente actual = frente;
        while (actual.getSiguiente() != null &&
                actual.getSiguiente().getCliente().getPrioridad() >= cliente.getPrioridad()) {
            actual = actual.getSiguiente();
        }

        nuevoNodo.setSiguiente(actual.getSiguiente());
        actual.setSiguiente(nuevoNodo);

        // Si se insertó al final, se actualiza el puntero ultimo
        if (nuevoNodo.getSiguiente() == null) {
            ultimo = nuevoNodo;
        }

        cantidadClientes++;
    }

    // Consulta y salidac

    /**
     * Retorna el cliente que está al frente de la cola (el próximo a
     * atender) sin removerlo de la cola.
     * @return Cliente al frente de la cola, o null si la cola está vacía
     */
    public Cliente verSiguiente() {
        if (estaVacia()) {
            return null;
        }
        return frente.getCliente();
    }

    /**
     * Remueve y retorna al cliente que está al frente de la cola
     * (el próximo a atender).
     * @return Cliente removido de la cola, o null si la cola está vacía
     */
    public Cliente desencolar() {
        if (estaVacia()) {
            return null;
        }

        Cliente cliente = frente.getCliente();
        frente = frente.getSiguiente();

        if (frente == null) {
            ultimo = null; // la cola quedó vacía
        }

        cantidadClientes--;
        return cliente;
    }

    // Visualización

    /**
     * Imprime en consola el estado actual de la cola, en orden de atención
     * (del frente hacia el final).
     */
    public void mostrarCola() {
        if (estaVacia()) {
            System.out.println("No hay clientes en la cola.\n");
            return;
        }

        System.out.println("=== COLA DE CLIENTES ===");
        NodoCliente tmp = frente;
        int posicion = 1;
        while (tmp != null) {
            System.out.println(posicion + ". " + tmp.getCliente());
            tmp = tmp.getSiguiente();
            posicion++;
        }
        System.out.println("Total de clientes en cola: " + cantidadClientes + "\n");
    }
}