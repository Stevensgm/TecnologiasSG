package controlador;

import dao.PedidoDAOImpl;
import dao.ProductoDAO;
import dao.ProductoDAOImpl;
import modelo.Pedido;
import modelo.DetallePedido;
import modelo.Producto;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la gestión de ventas.
 * Orquesta la lógica de negocio relacionada con pedidos y actualización de stock.
 */
public class VentaController {
    
    private final PedidoDAOImpl pedidoDAO;
    private final ProductoDAO productoDAO;

    public VentaController() {
        this.pedidoDAO = new PedidoDAOImpl();
        this.productoDAO = new ProductoDAOImpl();
    }

    /**
     * Procesa la venta completa: valida stock, guarda el pedido y actualiza inventario.
     * @param pedido El objeto Pedido con sus detalles.
     * @return true si la venta se procesó correctamente.
     */
    public boolean procesarVenta(Pedido pedido) {
        try {
            // PASO 1: Validar que hay suficiente stock para todos los productos
            if (!validarStockDisponible(pedido.getDetalles())) {
                System.err.println("❌ Error: Stock insuficiente para procesar la venta.");
                return false;
            }
            
            // PASO 2: Guardar el pedido en la base de datos
            boolean pedidoCreado = pedidoDAO.crear(pedido);
            
            if (!pedidoCreado) {
                System.err.println("❌ Error: No se pudo crear el pedido en la base de datos.");
                return false;
            }
            
            // PASO 3: Reducir el stock de los productos vendidos
            boolean stockActualizado = actualizarStockPostVenta(pedido.getDetalles());
            
            if (!stockActualizado) {
                System.err.println("⚠️ Advertencia: El pedido se creó pero hubo problemas al actualizar el stock.");
                // En un sistema real, aquí se debería hacer rollback del pedido
                return false;
            }
            
            System.out.println("✅ Venta procesada exitosamente. Pedido ID: " + pedido.getIdPedido());
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error de base de datos al procesar la venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error inesperado al procesar la venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Valida que haya suficiente stock para todos los productos del pedido.
     * @param detalles Lista de detalles del pedido
     * @return true si hay suficiente stock para todos los productos
     * @throws SQLException si hay error al consultar la base de datos
     */
    private boolean validarStockDisponible(List<DetallePedido> detalles) throws SQLException {
        if (detalles == null || detalles.isEmpty()) {
            System.err.println("❌ Error: No hay productos en el pedido.");
            return false;
        }
        
        for (DetallePedido detalle : detalles) {
            Producto producto = productoDAO.buscarPorId(detalle.getIdProducto());
            
            if (producto == null) {
                System.err.println("❌ Error: Producto ID " + detalle.getIdProducto() + " no existe.");
                return false;
            }
            
            if (producto.getStock() < detalle.getCantidad()) {
                System.err.println("❌ Error: Stock insuficiente para " + producto.getNombre() + 
                                 ". Disponible: " + producto.getStock() + ", Solicitado: " + detalle.getCantidad());
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Actualiza el stock de los productos después de procesar una venta.
     * IMPORTANTE: Esta es la corrección crítica - reduce el stock automáticamente.
     * @param detalles Lista de detalles del pedido
     * @return true si se actualizó correctamente el stock de todos los productos
     * @throws SQLException si hay error al actualizar la base de datos
     */
    private boolean actualizarStockPostVenta(List<DetallePedido> detalles) throws SQLException {
        for (DetallePedido detalle : detalles) {
            Producto producto = productoDAO.buscarPorId(detalle.getIdProducto());
            
            if (producto == null) {
                System.err.println("❌ Error: Producto ID " + detalle.getIdProducto() + " no encontrado.");
                return false;
            }
            
            // Calcular el nuevo stock (stock actual - cantidad vendida)
            int nuevoStock = producto.getStock() - detalle.getCantidad();
            
            if (nuevoStock < 0) {
                System.err.println("❌ Error: El stock no puede ser negativo para " + producto.getNombre());
                return false;
            }
            
            // Actualizar el stock en la base de datos
            boolean actualizado = productoDAO.actualizarStock(detalle.getIdProducto(), nuevoStock);
            
            if (!actualizado) {
                System.err.println("❌ Error: No se pudo actualizar el stock del producto ID " + detalle.getIdProducto());
                return false;
            }
            
            System.out.println("✅ Stock actualizado para " + producto.getNombre() + 
                             ". Stock anterior: " + producto.getStock() + 
                             ", Nuevo stock: " + nuevoStock);
        }
        
        return true;
    }
    
    /**
     * Método público para obtener un pedido por ID (útil para consultas futuras).
     * @param idPedido ID del pedido a buscar
     * @return El pedido encontrado o null si no existe
     */
    public Pedido buscarPedidoPorId(int idPedido) {
        try {
            return pedidoDAO.buscarPorId(idPedido);
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar pedido: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene todos los pedidos registrados.
     * @return Lista de todos los pedidos
     */
    public List<Pedido> obtenerTodosPedidos() {
        try {
            return pedidoDAO.obtenerTodos();
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener pedidos: " + e.getMessage());
            return null;
        }
    }
}