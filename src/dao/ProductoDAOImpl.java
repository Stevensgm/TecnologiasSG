package dao;

import modelo.Producto;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ProductoDAO que utiliza JDBC para interactuar con SQLite.
 */
public class ProductoDAOImpl implements ProductoDAO {

    private final Connection conn;

    public ProductoDAOImpl() {
        // Obtenemos la conexión establecida por la clase utilidad
        this.conn = ConexionDB.getConnection();
    }

    @Override
    public boolean crear(Producto producto) throws SQLException {
        String sql = "INSERT INTO Producto (nombre, descripcion, precio, stock, categoria) VALUES (?, ?, ?, ?, ?)";
        // Statement.RETURN_GENERATED_KEYS es necesario para obtener el ID autoincremental de SQLite
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());
            stmt.setString(5, producto.getCategoria());

            int filasAfectadas = stmt.executeUpdate();
            
            // Asignar el ID autogenerado
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        // SQLite devuelve el ID autogenerado en la primera columna
                        producto.setIdProducto(rs.getInt(1)); 
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Producto WHERE idProducto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("categoria")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Producto> obtenerTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto ORDER BY idProducto";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(new Producto(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                ));
            }
        }
        return productos;
    }

    @Override
    public boolean actualizarStock(int id, int nuevaCantidadStock) throws SQLException {
        String sql = "UPDATE Producto SET stock = ? WHERE idProducto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nuevaCantidadStock);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
}