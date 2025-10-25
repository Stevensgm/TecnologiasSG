package controlador;

import modelo.Producto;
import dao.ProductoDAO;
import dao.ProductoDAOImpl;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión del catálogo de productos.
 * Orquesta la lógica de negocio y la persistencia (DAO).
 */
public class ProductoController {

    private final ProductoDAO productoDAO; 

    public ProductoController() {
        this.productoDAO = new ProductoDAOImpl(); 
        
        // La inicialización ahora MANEJA la excepción para poder arrancar.
        try {
            if (productoDAO.obtenerTodos().isEmpty()) {
                inicializarCatalogo();
            }
        } catch (SQLException e) {
             System.err.println("❌ Error al verificar catálogo inicial: " + e.getMessage());
             // Nota: En una aplicación real, esto forzaría a salir o a mostrar un mensaje crítico.
        }
    }
    
    // -------------------------------------------------------------------
    // Métodos de Lectura
    // -------------------------------------------------------------------

    /**
     * Mantiene throws SQLException, ya que es la forma estándar de manejar excepciones JDBC en la capa Controller.
     */
    public List<Producto> obtenerCatalogo() throws SQLException {
        // CORRECCIÓN: Usamos el método que lanza la excepción
        return productoDAO.obtenerTodos();
    }
    
    /**
     * Nuevo método para que VentaController acceda al catálogo de forma eficiente (Map).
     */
    public Map<Integer, Producto> obtenerCatalogoMap() throws SQLException {
        return obtenerCatalogo().stream() // Llama al método que lanza la excepción
                .collect(Collectors.toMap(Producto::getIdProducto, Function.identity()));
    }
    
    // -------------------------------------------------------------------
    // Métodos de Gestión (Se mantienen iguales, ya que manejan la excepción internamente)
    // -------------------------------------------------------------------

    public String crearProducto(String nombre, String descripcion, double precio, int stock, String categoria) {
        
        if (nombre == null || nombre.trim().isEmpty() || precio <= 0 || stock < 0) {
            return "❌ Error de Validación: Nombre, precio (>0) y stock (>=0) son obligatorios y válidos.";
        }
        
        Producto nuevoProducto = new Producto(0, nombre, descripcion, precio, stock, categoria);

        try {
            if (productoDAO.crear(nuevoProducto)) {
                return "✅ Producto '" + nombre + "' creado con éxito en la DB. ID: " + nuevoProducto.getIdProducto();
            } else {
                return "❌ Error: No se pudo insertar el producto en la DB.";
            }
        } catch (SQLException e) {
            return "❌ Error de Base de Datos al crear producto: " + e.getMessage();
        }
    }

    public Producto buscarProductoPorId(int idProducto) {
        try {
            return productoDAO.buscarPorId(idProducto);
        } catch (SQLException e) {
            System.err.println("Error de DB al buscar producto: " + e.getMessage());
            return null;
        }
    }
    
    public String modificarStock(int idProducto, int cantidad) {
        try {
            Producto producto = buscarProductoPorId(idProducto);
            
            if (producto == null) {
                return "❌ Error: Producto ID " + idProducto + " no encontrado en DB.";
            }
            
            int nuevoStock = producto.getStock() + cantidad;
            if (nuevoStock < 0) {
                return "❌ Error: Operación resultaría en stock negativo. Stock actual: " + producto.getStock();
            }
            
            if (actualizarStockDirecto(idProducto, nuevoStock)) {
                 String operacion = cantidad > 0 ? "añadido(s)" : "reducido(s)";
                 return "✅ Stock de " + producto.getNombre() + " actualizado en DB. Se han " + operacion + " " + Math.abs(cantidad) + " unidades.";
            } else {
                return "❌ Error: No se pudo actualizar el stock en la DB.";
            }

        } catch (SQLException e) {
            return "❌ Error de Base de Datos al modificar stock: " + e.getMessage();
        }
    }
    
    /**
     * MÉTODO INTERNO: Actualiza el stock directamente. Usado por VentaController.
     */
    public boolean actualizarStockDirecto(int idProducto, int nuevoStock) throws SQLException {
        // CORRECCIÓN: Usamos el método que lanza la excepción
        return productoDAO.actualizarStock(idProducto, nuevoStock);
    }
    
    /**
     * Carga productos al inicio SOLO SI LA BASE DE DATOS está vacía.
     * CORRECCIÓN: Este método AHORA Lanza la excepción SQLException para que el constructor la capture.
     */
    private void inicializarCatalogo() throws SQLException {
        System.out.println("⚠️ Catálogo vacío detectado. Cargando datos iniciales...");
        // Aquí se llama a crearProducto, que maneja su propia excepción, pero
        // es más limpio lanzar la excepción si falla la inserción inicial.
        
        // Nota: Mantenemos el llamado a crearProducto que retorna String para ver los mensajes en consola.
        crearProducto("Tarjeta Gráfica RTX 4070", "GPU de alto rendimiento", 650.99, 10, "Componentes");
        crearProducto("Procesador Intel i7-14700K", "CPU de 14ª Generación", 380.50, 5, "Componentes");
        crearProducto("Memoria RAM 32GB DDR5 6000MHz", "Kit de 2x16GB", 95.00, 20, "Componentes");
        crearProducto("SSD M.2 NVMe 1TB", "Disco de estado sólido", 75.00, 15, "Almacenamiento");
    }
}