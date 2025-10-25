package dao;

import modelo.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    boolean crear(Usuario usuario) throws SQLException;
    Usuario buscarPorEmail(String email) throws SQLException;
    Usuario buscarPorId(int id) throws SQLException;
    List<Usuario> obtenerTodos() throws SQLException;
    // Métodos adicionales si se necesitan (actualizar, eliminar)
}