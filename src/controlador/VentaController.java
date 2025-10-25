package controlador;

import dao.PedidoDAOImpl; // Importación necesaria
import modelo.Pedido;
import modelo.DetallePedido;
import java.sql.SQLException;
import java.util.List;

public class VentaController {
    
    private final PedidoDAOImpl pedidoDAO;

    public VentaController() {
        this.pedidoDAO = new PedidoDAOImpl(); // Instanciación necesaria
    }

    /**
     * Procesa la venta, guardando el pedido y los detalles.
     * @param pedido El objeto Pedido con sus detalles.
     * @return true si la venta se procesó correctamente.
     */
    public boolean procesarVenta(Pedido pedido) {
        try {
            // Lógica de negocio (ej: verificar stock antes de guardar)
            
            return pedidoDAO.crear(pedido);
        } catch (SQLException e) {
            System.err.println("Error al procesar la venta: " + e.getMessage());
            return false;
        }
    }
}