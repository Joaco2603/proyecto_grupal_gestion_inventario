# Aplicación de Gestión de Inventarios

Proyecto de **Estructuras de Datos (SOFT-10)**, Universidad CENFOTEC.

## Funcionalidad

La aplicación permite registrar productos en un inventario implementado como
árbol binario de búsqueda, registrar clientes con prioridad y procesar sus
compras mediante una cola estable de prioridad.

- El árbol usa el nombre del producto como llave.
- Las prioridades son: 1 básico, 2 afiliado y 3 premium.
- Una prioridad mayor se atiende primero.
- En caso de empate se respeta el orden de llegada.
- Cada cliente posee un carrito basado en una lista enlazada.
- Al atender una compra se valida y descuenta el stock disponible.
- La factura muestra productos, cantidades, subtotales y total acumulado.

## Estructura principal

| Clase | Responsabilidad |
|---|---|
| `Producto` | Datos y costo de un producto |
| `NodoArbolProducto` / `ArbolProductos` | Inventario como árbol binario de búsqueda |
| `Cliente` | Identidad, prioridad y carrito |
| `NodoCliente` / `ColaClientes` | Cola enlazada de prioridad estable |
| `NodoProducto` / `ListaProductos` | Lista enlazada utilizada por los carritos |
| `Tienda` | Integración del inventario, clientes y compras |
| `Main` | Menú de consola y entrada del programa |

## Compilación y ejecución

Requiere Java 17 o superior.

```bash
javac src/*.java -d out
java -cp out Main
```

## Pruebas de estructuras e integración

```bash
javac src/*.java tests/InventoryTest.java -d /tmp/inventario-tests
java -cp /tmp/inventario-tests InventoryTest
```

## Entrega

Repositorio: https://github.com/Joaco2603/primer_avance_proyecto_grupal_gestion_inventario
