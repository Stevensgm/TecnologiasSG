package dao;

import modelo.Producto;
import java.util.List;
import java.sql.SQLException;

/**
 * Interfaz que define las operaciones CRUD para la entidad Producto.
 */
public interface ProductoDAO {
    
    // Operaciones CRUD
    boolean crear(Producto producto) throws SQLException;
    Producto buscarPorId(int id) throws SQLException;
    List<Producto> obtenerTodos() throws SQLException;
    // Actualiza el stock a un valor absoluto (usado por el Controller)
    boolean actualizarStock(int id, int nuevaCantidadStock) throws SQLException; 
}