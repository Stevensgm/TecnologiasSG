package dao;

import modelo.Pedido;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {
    
    /**
     * Guarda el pedido y sus detalles.
     * @param pedido El objeto Pedido a guardar.
     * @return true si la operación fue exitosa.
     * @throws SQLException 
     */
    boolean crear(Pedido pedido) throws SQLException;
    
    Pedido buscarPorId(int id) throws SQLException;
    
    List<Pedido> obtenerTodos() throws SQLException;
    
    // Puedes añadir más métodos como obtenerPedidosPorUsuario, etc.
}