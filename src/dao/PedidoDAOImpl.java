package dao;

import modelo.Pedido;
import modelo.DetallePedido;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class PedidoDAOImpl implements PedidoDAO {

    private final Connection conn;

    public PedidoDAOImpl() {
        this.conn = ConexionDB.getConnection();
    }

    @Override
    public boolean crear(Pedido pedido) throws SQLException {
        if (conn == null) return false;
        
        // La implementación completa requeriría transacciones 
        // para guardar el Pedido y luego el DetallePedido. 
        
        String sqlPedido = "INSERT INTO Pedido (idUsuario, fechaPedido, total) VALUES (?, ?, ?)";
        int idGenerado = -1;
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pedido.getIdUsuario());
            stmt.setString(2, pedido.getFechaPedido());
            stmt.setDouble(3, pedido.getTotal());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                        pedido.setIdPedido(idGenerado);
                        
                        // Si se generó el ID, procedemos a guardar los detalles
                        return guardarDetalles(idGenerado, pedido.getDetalles());
                    }
                }
            }
        }
        return false;
    }
    
    private boolean guardarDetalles(int idPedido, List<DetallePedido> detalles) throws SQLException {
        if (conn == null || detalles == null || detalles.isEmpty()) return false;
        
        String sqlDetalle = "INSERT INTO DetallePedido (idPedido, idProducto, cantidad, precioUnitario) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlDetalle)) {
            for (DetallePedido detalle : detalles) {
                stmt.setInt(1, idPedido);
                stmt.setInt(2, detalle.getIdProducto());
                stmt.setInt(3, detalle.getCantidad());
                stmt.setDouble(4, detalle.getPrecioUnitario());
                stmt.addBatch(); // Agregar al lote
            }
            int[] results = stmt.executeBatch();
            return results.length == detalles.size(); // Verificar que todos se insertaron
        }
    }
    
    // Métodos placeholder
    @Override
    public Pedido buscarPorId(int id) throws SQLException {
        // Implementación pendiente
        return null; 
    }

    @Override
    public List<Pedido> obtenerTodos() throws SQLException {
        // Implementación pendiente
        return new ArrayList<>(); 
    }
}