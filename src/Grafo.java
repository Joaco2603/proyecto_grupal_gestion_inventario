/**
 * Grafo ponderado y no dirigido para almacenar ubicaciones y distancias.
 * Los vértices y las aristas se guardan en listas enlazadas simples.
 */
public class Grafo {
    private VerticeGrafo primerVertice;
    private int cantidadVertices;
    private int cantidadAristas;

    /** Crea un grafo vacío. */
    public Grafo() {
        primerVertice = null;
        cantidadVertices = 0;
        cantidadAristas = 0;
    }

    public boolean estaVacio() {
        return primerVertice == null;
    }

    public int cantidadVertices() {
        return cantidadVertices;
    }

    public int cantidadAristas() {
        return cantidadAristas;
    }

    /** Agrega una ubicación si todavía no existe. */
    public boolean agregarVertice(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            return false;
        }

        ubicacion = ubicacion.trim();
        if (buscarVertice(ubicacion) != null) {
            return false;
        }

        VerticeGrafo nuevo = new VerticeGrafo(ubicacion);
        if (primerVertice == null) {
            primerVertice = nuevo;
        } else {
            VerticeGrafo actual = primerVertice;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }

        cantidadVertices++;
        return true;
    }

    public boolean contieneVertice(String ubicacion) {
        return buscarVertice(ubicacion) != null;
    }

    /**
     * Agrega una conexión en las dos direcciones. Ambos vértices deben existir
     * y la distancia debe ser mayor que cero.
     */
    public boolean agregarArista(String origen, String destino, double distancia) {
        if (origen == null || destino == null || origen.isBlank() || destino.isBlank() ||
                origen.trim().equalsIgnoreCase(destino.trim()) ||
                !Double.isFinite(distancia) || distancia <= 0) {
            return false;
        }

        VerticeGrafo verticeOrigen = buscarVertice(origen);
        VerticeGrafo verticeDestino = buscarVertice(destino);
        if (verticeOrigen == null || verticeDestino == null ||
                buscarArista(verticeOrigen, verticeDestino) != null) {
            return false;
        }

        AristaGrafo aristaIda = new AristaGrafo(verticeDestino, distancia);
        aristaIda.setSiguiente(verticeOrigen.getPrimeraArista());
        verticeOrigen.setPrimeraArista(aristaIda);

        AristaGrafo aristaVuelta = new AristaGrafo(verticeOrigen, distancia);
        aristaVuelta.setSiguiente(verticeDestino.getPrimeraArista());
        verticeDestino.setPrimeraArista(aristaVuelta);

        cantidadAristas++;
        return true;
    }

    public boolean existeArista(String origen, String destino) {
        VerticeGrafo verticeOrigen = buscarVertice(origen);
        VerticeGrafo verticeDestino = buscarVertice(destino);
        return verticeOrigen != null && verticeDestino != null &&
                buscarArista(verticeOrigen, verticeDestino) != null;
    }

    /** Retorna la distancia directa o infinito cuando no existe la arista. */
    public double obtenerDistancia(String origen, String destino) {
        VerticeGrafo verticeOrigen = buscarVertice(origen);
        VerticeGrafo verticeDestino = buscarVertice(destino);
        if (verticeOrigen == null || verticeDestino == null) {
            return Double.POSITIVE_INFINITY;
        }

        AristaGrafo arista = buscarArista(verticeOrigen, verticeDestino);
        if (arista == null) {
            return Double.POSITIVE_INFINITY;
        }
        return arista.getDistancia();
    }

    /** Calcula el camino más corto entre dos ubicaciones usando Dijkstra. */
    public Ruta dijkstra(String origen, String destino) {
        VerticeGrafo inicio = buscarVertice(origen);
        VerticeGrafo fin = buscarVertice(destino);
        if (inicio == null || fin == null) {
            return new Ruta("", Double.POSITIVE_INFINITY, false);
        }

        VerticeGrafo vertice = primerVertice;
        while (vertice != null) {
            vertice.setVisitado(false);
            vertice.setDistanciaTemporal(Double.POSITIVE_INFINITY);
            vertice.setAnterior(null);
            vertice = vertice.getSiguiente();
        }
        inicio.setDistanciaTemporal(0);

        while (true) {
            VerticeGrafo actual = null;
            double menorDistancia = Double.POSITIVE_INFINITY;

            vertice = primerVertice;
            while (vertice != null) {
                if (!vertice.isVisitado() && vertice.getDistanciaTemporal() < menorDistancia) {
                    actual = vertice;
                    menorDistancia = vertice.getDistanciaTemporal();
                }
                vertice = vertice.getSiguiente();
            }

            if (actual == null) {
                break;
            }

            actual.setVisitado(true);
            if (actual == fin) {
                break;
            }

            AristaGrafo arista = actual.getPrimeraArista();
            while (arista != null) {
                VerticeGrafo vecino = arista.getDestino();
                double nuevaDistancia = actual.getDistanciaTemporal() + arista.getDistancia();
                if (!vecino.isVisitado() && nuevaDistancia < vecino.getDistanciaTemporal()) {
                    vecino.setDistanciaTemporal(nuevaDistancia);
                    vecino.setAnterior(actual);
                }
                arista = arista.getSiguiente();
            }
        }

        if (Double.isInfinite(fin.getDistanciaTemporal())) {
            return new Ruta("", Double.POSITIVE_INFINITY, false);
        }

        String camino = "";
        vertice = fin;
        while (vertice != null) {
            if (camino.isEmpty()) {
                camino = vertice.getUbicacion();
            } else {
                camino = vertice.getUbicacion() + " -> " + camino;
            }
            if (vertice == inicio) {
                break;
            }
            vertice = vertice.getAnterior();
        }

        return new Ruta(camino, fin.getDistanciaTemporal(), true);
    }

    /** Retorna las ubicaciones en un texto sencillo para el menú. */
    public String obtenerUbicaciones() {
        String resultado = "";
        VerticeGrafo actual = primerVertice;
        while (actual != null) {
            if (!resultado.isEmpty()) {
                resultado += ", ";
            }
            resultado += actual.getUbicacion();
            actual = actual.getSiguiente();
        }
        return resultado;
    }

    /** Agrega las ubicaciones y rutas iniciales de la aplicación. */
    public void cargarMapaBase() {
        agregarVertice("Tienda Central");
        agregarVertice("Centro de Distribución");
        agregarVertice("Barrio Norte");
        agregarVertice("Barrio Sur");
        agregarVertice("Zona Este");

        agregarArista("Tienda Central", "Centro de Distribución", 4);
        agregarArista("Centro de Distribución", "Barrio Norte", 6);
        agregarArista("Centro de Distribución", "Barrio Sur", 5);
        agregarArista("Barrio Sur", "Zona Este", 3);
    }

    private VerticeGrafo buscarVertice(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            return null;
        }

        VerticeGrafo actual = primerVertice;
        while (actual != null) {
            if (actual.getUbicacion().equalsIgnoreCase(ubicacion.trim())) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    private AristaGrafo buscarArista(VerticeGrafo origen, VerticeGrafo destino) {
        AristaGrafo actual = origen.getPrimeraArista();
        while (actual != null) {
            if (actual.getDestino() == destino) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }
}
