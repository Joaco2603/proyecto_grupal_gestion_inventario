# Aplicación de Gestión de Inventarios

Aplicación de consola para el curso **Estructuras de Datos (SOFT-10)** de la
Universidad CENFOTEC.

## Funcionalidades principales

- Inventario de productos implementado con un árbol binario de búsqueda.
- Cola de clientes ordenada por prioridad y por orden de llegada.
- Carrito de compras basado en una lista enlazada.
- Descuento de existencias al completar una compra.
- Grafo ponderado y no dirigido para representar las ubicaciones.
- Búsqueda de la ruta más corta con el algoritmo de Dijkstra.
- Validación para impedir la atención de clientes sin ruta de entrega.
- Factura con productos, total, camino de entrega y distancia.
- Menú para consultar, agregar y conectar ubicaciones.

## Estructuras principales

| Clase | Responsabilidad |
|---|---|
| `Producto` | Datos, precio, cantidad e imágenes de un producto |
| `ArbolProductos` | Inventario ordenado por nombre |
| `Cliente` | Identidad, prioridad, ubicación y carrito |
| `ColaClientes` | Atención prioritaria de clientes |
| `ListaProductos` | Lista enlazada para los carritos |
| `Grafo` | Ubicaciones, conexiones y rutas |
| `VerticeGrafo` | Ubicación y conexiones enlazadas |
| `AristaGrafo` | Destino y distancia de una conexión |
| `Ruta` | Camino y distancia calculados por Dijkstra |
| `Tienda` | Integración del inventario, clientes y entregas |
| `Main` | Menú y ejecución de la aplicación |

## Ejecución

Requiere Java 17 o superior.

```bash
javac src/*.java -d out
java -cp out Main
```

## Pruebas

```bash
javac src/*.java tests/InventoryTest.java -d /tmp/inventario-tests
java -ea -cp /tmp/inventario-tests InventoryTest
```

Las pruebas verifican el árbol, la cola prioritaria, las compras, Dijkstra,
las rutas desconectadas, la integración con la tienda y la factura de entrega.

## Entrega

Repositorio: https://github.com/Joaco2603/proyecto_grupal_gestion_inventario
