package dao;

import modelo.Usuario;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class UsuarioDAOImpl implements UsuarioDAO {

    private final Connection conn;

    public UsuarioDAOImpl() {
        this.conn = ConexionDB.getConnection();
        try {
            if (this.conn != null) {
                inicializarUsuarios();
            }
        } catch (SQLException e) {
            System.err.println("❌ ERROR al inicializar usuarios de prueba: " + e.getMessage());
        }
    }
    
    // --- SIMULACIÓN DE SEGURIDAD ---
    private String hashPassword(String password) {
        // Simulación: password en minúsculas
        return password.toLowerCase(); 
    }
    
    public String hashPasswordParaRegistro(String password) {
        return hashPassword(password);
    }
    
    private boolean verificarPassword(String passwordIngresada, String hashAlmacenado) {
        return hashPassword(passwordIngresada).equals(hashAlmacenado);
    }
    
    // --- Inicialización de Usuarios de Prueba ---
    private void inicializarUsuarios() throws SQLException {
        if (conn == null) return;
        
        String passHashDefault = hashPassword("123456"); 

        if (buscarPorEmail("admin@sg.com") == null) {
            System.out.println("⚠️ Creando usuario Administrador inicial (admin@sg.com / 123456)...");
            Usuario admin = new Usuario("Admin SG", "admin@sg.com", passHashDefault, "Administrador");
            crear(admin);
        }
        
        if (buscarPorEmail("cliente.prueba@sg.com") == null) {
            System.out.println("⚠️ Creando usuario Cliente inicial (cliente.prueba@sg.com / 123456)...");
            Usuario cliente = new Usuario("Cliente Venta", "cliente.prueba@sg.com", passHashDefault, "Cliente");
            crear(cliente);
        }
    }
    
    @Override
    public boolean crear(Usuario usuario) throws SQLException {
        if (conn == null) return false;
        String sql = "INSERT INTO Usuario (nombre, email, password_hash, rol) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPasswordHash()); 
            stmt.setString(4, usuario.getRol());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Usuario buscarPorEmail(String email) throws SQLException {
        if (conn == null) return null;
        String sql = "SELECT idUsuario, nombre, email, password_hash, rol FROM Usuario WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password_hash"), 
                        rs.getString("rol")
                    );
                }
            }
        }
        return null;
    }
    
    public Usuario autenticar(String email, String password) throws SQLException {
        Usuario usuario = buscarPorEmail(email);
        
        if (usuario != null) {
            if (verificarPassword(password, usuario.getPasswordHash())) {
                return usuario; 
            }
        }
        return null; 
    }
    
    // Otros métodos omitidos por espacio.
    @Override public Usuario buscarPorId(int id) throws SQLException { return null; }
    @Override public List<Usuario> obtenerTodos() throws SQLException { return new ArrayList<>(); }
}