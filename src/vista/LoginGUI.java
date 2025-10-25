package vista;

import controlador.ProductoController;
import controlador.VentaController;
import modelo.Usuario;
import dao.UsuarioDAOImpl;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginGUI extends JFrame {

    private final ProductoController productoController;
    private final VentaController ventaController;
    private final UsuarioDAOImpl usuarioDAO;

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLoginAdmin;
    private JButton btnLoginCliente;
    private JButton btnRegistro;
    private JLabel lblEstado;

    public LoginGUI(ProductoController productoController, VentaController ventaController) {
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.usuarioDAO = new UsuarioDAOImpl();
        
        setTitle("Tecnología SG - Sistema de Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
        
        setSize(500, 380); 
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel lblTitulo = new JLabel("TECNOLOGÍA SG", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Ventas", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 1;
        panel.add(lblSubtitulo, gbc);

        // Espacio
        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(10), gbc);

        // Email
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Correo Electrónico:"), gbc);

        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        // Password
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // Espacio
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(Box.createVerticalStrut(10), gbc);

        // Botones de Login
        btnLoginAdmin = new JButton("🔧 Acceder como Administrador");
        btnLoginCliente = new JButton("🛒 Acceder como Cliente");
        
        gbc.gridy = 6;
        panel.add(btnLoginAdmin, gbc);

        gbc.gridy = 7;
        panel.add(btnLoginCliente, gbc);

        // Separador
        JSeparator separator = new JSeparator();
        gbc.gridy = 8;
        panel.add(separator, gbc);

        // Botón de Registro
        btnRegistro = new JButton("📝 ¿Nuevo cliente? Regístrate aquí");
        gbc.gridy = 9;
        panel.add(btnRegistro, gbc);

        // Label de estado
        lblEstado = new JLabel("Credenciales de prueba: admin@sg.com / 123456", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.ITALIC, 10));
        lblEstado.setForeground(Color.GRAY);
        gbc.gridy = 10;
        panel.add(lblEstado, gbc);

        add(panel);
        
        // Listeners
        btnLoginAdmin.addActionListener(e -> intentarLogin("Administrador"));
        btnLoginCliente.addActionListener(e -> intentarLogin("Cliente"));
        
        btnRegistro.addActionListener(e -> {
            this.setVisible(false); 
            new RegistroGUI(this).setVisible(true);
        });

        // Permitir login con Enter
        txtPassword.addActionListener(e -> {
            // Intentar login como cliente por defecto al presionar Enter
            intentarLogin("Cliente");
        });
    }

    private void intentarLogin(String rolRequerido) {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim(); 
        
        // Validación de campos vacíos
        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingrese correo y contraseña.");
            return;
        }

        try {
            // Intentar autenticar
            Usuario usuario = usuarioDAO.autenticar(email, password);

            if (usuario == null) {
                mostrarError("Correo o contraseña incorrectos.\nVerifique sus credenciales.");
                limpiarPassword();
                return;
            }

            // Validar que el rol coincida
            if (!usuario.getRol().equals(rolRequerido)) {
                mostrarError("Credenciales válidas, pero su rol es: " + usuario.getRol() + 
                           "\n\nDebe acceder con el botón correspondiente a su rol.");
                limpiarPassword();
                return;
            }

            // Login exitoso
            mostrarExito("¡Bienvenido, " + usuario.getNombre() + "!");
            
            // Cerrar ventana de login
            this.dispose(); 
            
            // Abrir la ventana correspondiente según el rol
            if (rolRequerido.equals("Administrador")) {
                SwingUtilities.invokeLater(() -> {
                    AdminGUI adminGUI = new AdminGUI(productoController);
                    adminGUI.setVisible(true);
                });
            } else if (rolRequerido.equals("Cliente")) {
                // CORRECCIÓN: Ahora pasamos el ID del usuario al ClienteGUI
                SwingUtilities.invokeLater(() -> {
                    ClienteGUI clienteGUI = new ClienteGUI(productoController, ventaController, usuario.getIdUsuario());
                    clienteGUI.setVisible(true);
                });
            }

        } catch (SQLException e) {
            mostrarError("Error de base de datos al iniciar sesión:\n" + e.getMessage());
            System.err.println("❌ SQLException en login: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado al iniciar sesión:\n" + e.getMessage());
            System.err.println("❌ Exception en login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblEstado.setText("❌ Error de acceso");
        lblEstado.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, mensaje, "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        lblEstado.setText("✅ Acceso exitoso");
        lblEstado.setForeground(new Color(0, 150, 0));
        JOptionPane.showMessageDialog(this, mensaje, "Acceso Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarPassword() {
        txtPassword.setText("");
        txtPassword.requestFocus();
    }
}