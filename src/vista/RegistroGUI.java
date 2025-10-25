package vista;

import modelo.Usuario;
import dao.UsuarioDAOImpl;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegistroGUI extends JFrame {

    private final UsuarioDAOImpl usuarioDAO;
    private final JFrame loginFrame;

    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> comboRol;
    private JButton btnRegistrar;
    private JButton btnVolver;
    private JLabel lblDescripcionRol;

    public RegistroGUI(JFrame loginFrame) {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.loginFrame = loginFrame;
        
        setTitle("Tecnología SG - Registro de Usuario");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        
        initComponents();
        
        setSize(500, 480);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel lblTitulo = new JLabel("REGISTRO DE NUEVO USUARIO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Espacio
        gbc.gridy = 1;
        panel.add(Box.createVerticalStrut(10), gbc);

        // Nombre
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Nombre Completo:"), gbc);

        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);
        
        // Email
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Correo Electrónico:"), gbc);

        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        
        // Contraseña
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Contraseña (mín. 6):"), gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        // Confirmar Contraseña
        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Confirmar Contraseña:"), gbc);

        txtConfirmPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtConfirmPassword, gbc);

        // Selector de Rol - NUEVO
        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel lblRol = new JLabel("Tipo de Usuario:");
        lblRol.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblRol, gbc);

        String[] roles = {"Cliente", "Administrador"};
        comboRol = new JComboBox<>(roles);
        comboRol.setSelectedIndex(0); // Cliente por defecto
        gbc.gridx = 1;
        panel.add(comboRol, gbc);

        // Descripción del rol seleccionado
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        lblDescripcionRol = new JLabel();
        lblDescripcionRol.setFont(new Font("Arial", Font.ITALIC, 11));
        lblDescripcionRol.setForeground(new Color(70, 130, 180));
        actualizarDescripcionRol();
        panel.add(lblDescripcionRol, gbc);

        // Listener para cambio de rol
        comboRol.addActionListener(e -> actualizarDescripcionRol());

        // Panel de información
        gbc.gridy = 8;
        JPanel panelInfo = crearPanelInformacion();
        panel.add(panelInfo, gbc);

        // Espacio
        gbc.gridy = 9;
        panel.add(Box.createVerticalStrut(10), gbc);

        // Botones
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        btnVolver = new JButton("⬅️ Volver al Login");
        panel.add(btnVolver, gbc);

        btnRegistrar = new JButton("✅ Registrar Cuenta");
        btnRegistrar.setBackground(new Color(0, 150, 0));
        btnRegistrar.setForeground(Color.WHITE);
        gbc.gridx = 1;
        panel.add(btnRegistrar, gbc);

        add(panel);

        // Listeners
        btnRegistrar.addActionListener(e -> intentarRegistro());
        
        btnVolver.addActionListener(e -> {
            this.dispose();
            loginFrame.setVisible(true); 
        });

        // Permitir registro con Enter
        txtConfirmPassword.addActionListener(e -> intentarRegistro());
    }

    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Diferencias entre roles"));
        panel.setBackground(new Color(245, 245, 245));

        JLabel lblCliente = new JLabel("🛒 Cliente: Puede comprar productos, ver catálogo y gestionar su carrito");
        lblCliente.setFont(new Font("Arial", Font.PLAIN, 10));
        
        JLabel lblAdmin = new JLabel("🔧 Administrador: Puede gestionar productos (crear, modificar, actualizar stock)");
        lblAdmin.setFont(new Font("Arial", Font.PLAIN, 10));

        panel.add(lblCliente);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblAdmin);

        return panel;
    }

    private void actualizarDescripcionRol() {
        String rolSeleccionado = (String) comboRol.getSelectedItem();
        
        if ("Cliente".equals(rolSeleccionado)) {
            lblDescripcionRol.setText("🛒 Podrás ver productos, agregar al carrito y realizar compras");
        } else {
            lblDescripcionRol.setText("🔧 Podrás gestionar el catálogo de productos y modificar inventario");
        }
    }

    private void intentarRegistro() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String rolSeleccionado = (String) comboRol.getSelectedItem();

        // 1. Validaciones (SCE2.12)
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden.");
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            txtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.");
            txtPassword.requestFocus();
            return;
        }
        
        // Validación de formato de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError("Ingrese un formato de correo electrónico válido.\nEjemplo: usuario@dominio.com");
            txtEmail.requestFocus();
            return;
        }

        // Confirmación especial para administradores
        if ("Administrador".equals(rolSeleccionado)) {
            int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de que desea registrarse como ADMINISTRADOR?\n\n" +
                "Los administradores tienen acceso completo al sistema:\n" +
                "• Crear nuevos productos\n" +
                "• Modificar precios\n" +
                "• Actualizar stock\n" +
                "• Editar información de productos\n\n" +
                "Esta acción requiere responsabilidad.", 
                "Confirmar Registro como Administrador", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            // 2. Verificar si el email ya existe
            if (usuarioDAO.buscarPorEmail(email) != null) {
                mostrarError("El correo electrónico ya está registrado.\nIntente con otro correo.");
                txtEmail.requestFocus();
                return;
            }
            
            // 3. Crear nuevo usuario con el rol seleccionado
            String passHash = usuarioDAO.hashPasswordParaRegistro(password);
            Usuario nuevoUsuario = new Usuario(nombre, email, passHash, rolSeleccionado);
            
            if (usuarioDAO.crear(nuevoUsuario)) {
                String mensaje = rolSeleccionado.equals("Cliente") ?
                    "✅ ¡Registro exitoso!\n\n" +
                    "Tu cuenta de CLIENTE ha sido creada.\n" +
                    "Ya puedes iniciar sesión y comenzar a comprar.\n\n" +
                    "Credenciales:\n" +
                    "Email: " + email :
                    
                    "✅ ¡Registro exitoso!\n\n" +
                    "Tu cuenta de ADMINISTRADOR ha sido creada.\n" +
                    "Ya puedes iniciar sesión y gestionar el catálogo.\n\n" +
                    "Credenciales:\n" +
                    "Email: " + email;

                JOptionPane.showMessageDialog(this, mensaje, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                loginFrame.setVisible(true);
            } else {
                mostrarError("Error desconocido al registrar el usuario.\nIntente nuevamente.");
            }

        } catch (SQLException e) {
            mostrarError("Error de base de datos:\n" + e.getMessage());
            System.err.println("❌ SQLException en registro: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado:\n" + e.getMessage());
            System.err.println("❌ Exception en registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }
}