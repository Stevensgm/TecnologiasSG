package dao;

import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que simula procedimientos almacenados para SQLite.
 * 
 * SQLite NO soporta procedimientos almacenados nativos (STORED PROCEDURES),
 * pero podemos encapsular lógica compleja en métodos Java que ejecutan
 * múltiples operaciones SQL de manera transaccional.
 * 
 * Esta clase cumple con el requisito SCE2.19 de "Implementación de procedimientos almacenados"
 * adaptado a las limitaciones de SQLite.
 */
public class ProcedimientosAlmacenados {

    private final Connection conn;

    public ProcedimientosAlmacenados() {
        this.conn = ConexionDB.getConnection();
    }

    /**
     * PROCEDIMIENTO 1: Obtener estadísticas de ventas por categoría.
     * Simula un SP que calcula el total vendido por cada categoría de productos.
     * 
     * @return Map con la categoría como clave y el total vendido como valor
     * @throws SQLException si hay error en la consulta
     */
    public Map<String, Double> obtenerVentasPorCategoria() throws SQLException {
        Map<String, Double> resultado = new HashMap<>();
        
        String sql = "SELECT p.categoria, SUM(dp.cantidad * dp.precioUnitario) as total " +
                    "FROM DetallePedido dp " +
                    "INNER JOIN Producto p ON dp.idProducto = p.idProducto " +
                    "GROUP BY p.categoria " +
                    "ORDER BY total DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String categoria = rs.getString("categoria");
                double total = rs.getDouble("total");
                resultado.put(categoria, total);
            }
        }
        
        return resultado;
    }

    /**
     * PROCEDIMIENTO 2: Obtener productos con stock bajo (menor a un umbral).
     * Simula un SP que identifica productos que necesitan reabastecimiento.
     * 
     * @param umbralStock El nivel de stock considerado como "bajo"
     * @return Map con el ID del producto y su stock actual
     * @throws SQLException si hay error en la consulta
     */
    public Map<Integer, Integer> obtenerProductosConStockBajo(int umbralStock) throws SQLException {
        Map<Integer, Integer> resultado = new HashMap<>();
        
        String sql = "SELECT idProducto, nombre, stock " +
                    "FROM Producto " +
                    "WHERE stock < ? " +
                    "ORDER BY stock ASC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, umbralStock);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idProducto = rs.getInt("idProducto");
                    int stock = rs.getInt("stock");
                    resultado.put(idProducto, stock);
                    
                    System.out.println("⚠️ ALERTA: Producto ID " + idProducto + " (" + 
                                     rs.getString("nombre") + ") tiene stock bajo: " + stock + " unidades");
                }
            }
        }
        
        return resultado;
    }

    /**
     * PROCEDIMIENTO 3: Obtener el total de ventas de un cliente específico.
     * Simula un SP que calcula cuánto ha gastado un cliente en total.
     * 
     * @param idUsuario ID del usuario/cliente
     * @return Total gastado por el cliente
     * @throws SQLException si hay error en la consulta
     */
    public double obtenerTotalVentasCliente(int idUsuario) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) as totalGastado " +
                    "FROM Pedido " +
                    "WHERE idUsuario = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("totalGastado");
                }
            }
        }
        
        return 0.0;
    }

    /**
     * PROCEDIMIENTO 4: Obtener reporte de ventas por período.
     * Simula un SP que genera un reporte de ventas entre dos fechas.
     * 
     * @param fechaInicio Fecha de inicio (formato: YYYY-MM-DD)
     * @param fechaFin Fecha de fin (formato: YYYY-MM-DD)
     * @return Map con datos del reporte (cantidadPedidos, totalVentas, etc.)
     * @throws SQLException si hay error en la consulta
     */
    public Map<String, Object> obtenerReporteVentasPorPeriodo(String fechaInicio, String fechaFin) throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(idPedido) as cantidadPedidos, " +
                    "SUM(total) as totalVentas, " +
                    "AVG(total) as promedioVenta, " +
                    "MAX(total) as ventaMayor, " +
                    "MIN(total) as ventaMenor " +
                    "FROM Pedido " +
                    "WHERE DATE(fechaPedido) BETWEEN DATE(?) AND DATE(?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fechaInicio);
            stmt.setString(2, fechaFin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reporte.put("cantidadPedidos", rs.getInt("cantidadPedidos"));
                    reporte.put("totalVentas", rs.getDouble("totalVentas"));
                    reporte.put("promedioVenta", rs.getDouble("promedioVenta"));
                    reporte.put("ventaMayor", rs.getDouble("ventaMayor"));
                    reporte.put("ventaMenor", rs.getDouble("ventaMenor"));
                }
            }
        }
        
        return reporte;
    }

    /**
     * PROCEDIMIENTO 5: Actualizar precios por categoría (operación masiva).
     * Simula un SP que aplica un ajuste porcentual a todos los productos de una categoría.
     * 
     * @param categoria La categoría de productos a actualizar
     * @param porcentajeAjuste El porcentaje de ajuste (positivo o negativo)
     * @return Cantidad de productos actualizados
     * @throws SQLException si hay error en la actualización
     */
    public int actualizarPreciosPorCategoria(String categoria, double porcentajeAjuste) throws SQLException {
        if (conn == null) {
            throw new SQLException("Conexión a la base de datos no disponible");
        }
        
        String sql = "UPDATE Producto " +
                    "SET precio = precio * (1 + ? / 100.0) " +
                    "WHERE categoria = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, porcentajeAjuste);
            stmt.setString(2, categoria);
            
            int filasActualizadas = stmt.executeUpdate();
            
            if (filasActualizadas > 0) {
                System.out.println("✅ Se actualizaron " + filasActualizadas + 
                                 " productos de la categoría '" + categoria + 
                                 "' con un ajuste del " + porcentajeAjuste + "%");
            }
            
            return filasActualizadas;
        }
    }

    /**
     * PROCEDIMIENTO 6: Obtener el top N de productos más vendidos.
     * Simula un SP que retorna los productos más vendidos.
     * 
     * @param limite Cantidad de productos a retornar
     * @return Map con el ID del producto y la cantidad total vendida
     * @throws SQLException si hay error en la consulta
     */
    public Map<Integer, Integer> obtenerTopProductosMasVendidos(int limite) throws SQLException {
        Map<Integer, Integer> resultado = new HashMap<>();
        
        String sql = "SELECT p.idProducto, p.nombre, SUM(dp.cantidad) as totalVendido " +
                    "FROM DetallePedido dp " +
                    "INNER JOIN Producto p ON dp.idProducto = p.idProducto " +
                    "GROUP BY p.idProducto, p.nombre " +
                    "ORDER BY totalVendido DESC " +
                    "LIMIT ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n📊 TOP " + limite + " PRODUCTOS MÁS VENDIDOS:");
                int posicion = 1;
                
                while (rs.next()) {
                    int idProducto = rs.getInt("idProducto");
                    int totalVendido = rs.getInt("totalVendido");
                    String nombre = rs.getString("nombre");
                    
                    resultado.put(idProducto, totalVendido);
                    System.out.println(posicion + ". " + nombre + " - " + totalVendido + " unidades vendidas");
                    posicion++;
                }
            }
        }
        
        return resultado;
    }

    /**
     * PROCEDIMIENTO 7: Limpieza de pedidos antiguos (mantenimiento).
     * Simula un SP que elimina pedidos más antiguos que cierta fecha.
     * NOTA: En producción, esto debería hacerse con mucho cuidado y respaldos.
     * 
     * @param fechaLimite Fecha antes de la cual se eliminarán los pedidos (formato: YYYY-MM-DD)
     * @return Cantidad de pedidos eliminados
     * @throws SQLException si hay error en la eliminación
     */
    public int limpiarPedidosAntiguos(String fechaLimite) throws SQLException {
        if (conn == null) {
            throw new SQLException("Conexión a la base de datos no disponible");
        }
        
        // Primero eliminar los detalles de los pedidos antiguos
        String sqlDetalles = "DELETE FROM DetallePedido " +
                           "WHERE idPedido IN (SELECT idPedido FROM Pedido WHERE DATE(fechaPedido) < DATE(?))";
        
        // Luego eliminar los pedidos
        String sqlPedidos = "DELETE FROM Pedido WHERE DATE(fechaPedido) < DATE(?)";
        
        int pedidosEliminados = 0;
        
        try (PreparedStatement stmtDetalles = conn.prepareStatement(sqlDetalles);
             PreparedStatement stmtPedidos = conn.prepareStatement(sqlPedidos)) {
            
            // Transacción manual
            conn.setAutoCommit(false);
            
            try {
                // Eliminar detalles
                stmtDetalles.setString(1, fechaLimite);
                stmtDetalles.executeUpdate();
                
                // Eliminar pedidos
                stmtPedidos.setString(1, fechaLimite);
                pedidosEliminados = stmtPedidos.executeUpdate();
                
                conn.commit();
                
                if (pedidosEliminados > 0) {
                    System.out.println("🗑️ Se eliminaron " + pedidosEliminados + 
                                     " pedidos anteriores a " + fechaLimite);
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        
        return pedidosEliminados;
    }
}