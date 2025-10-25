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

    public LoginGUI(ProductoController productoController, VentaController ventaController) {
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.usuarioDAO = new UsuarioDAOImpl();
        
        setTitle("Tecnología SG - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
        
        setSize(450, 300); 
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtEmail = new JTextField(20);
        txtPassword = new JPasswordField(20);
        btnLoginAdmin = new JButton("Acceder como Administrador");
        btnLoginCliente = new JButton("Acceder como Cliente");
        btnRegistro = new JButton("¿Nuevo cliente? Regístrate aquí.");

        panel.add(new JLabel("Correo Electrónico:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtPassword); 
        
        JPanel panelLoginBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelLoginBotones.add(btnLoginAdmin);
        panelLoginBotones.add(btnLoginCliente);
        
        panel.add(panelLoginBotones); 
        panel.add(btnRegistro);        

        add(panel);
        
        btnLoginAdmin.addActionListener(e -> intentarLogin("Administrador"));
        btnLoginCliente.addActionListener(e -> intentarLogin("Cliente"));
        
        btnRegistro.addActionListener(e -> {
            this.setVisible(false); 
            new RegistroGUI(this).setVisible(true);
        });
    }

    private void intentarLogin(String rolRequerido) {
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword()); 
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar correo y contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(email, password);

            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!usuario.getRol().equals(rolRequerido)) {
                JOptionPane.showMessageDialog(this, "Credenciales válidas, pero el rol es incorrecto para este acceso (" + usuario.getRol() + ").", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.dispose(); 
            
            if (rolRequerido.equals("Administrador")) {
                new AdminGUI(productoController).setVisible(true);
            } else {
                new ClienteGUI(productoController, ventaController).setVisible(true); 
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos al iniciar sesión: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
        }
    }
}