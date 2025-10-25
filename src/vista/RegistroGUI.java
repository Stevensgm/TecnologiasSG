package vista;

import modelo.Usuario;
import dao.UsuarioDAOImpl;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegistroGUI extends JFrame {

    private final UsuarioDAOImpl usuarioDAO;

    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegistrar;
    private JButton btnVolver;

    public RegistroGUI(JFrame loginFrame) {
        this.usuarioDAO = new UsuarioDAOImpl();
        
        setTitle("Tecnología SG - Registro de Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        
        initComponents(loginFrame);
        
        setSize(450, 350);
        setLocationRelativeTo(null);
    }

    private void initComponents(JFrame loginFrame) {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtNombre = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtConfirmPassword = new JPasswordField(20);
        btnRegistrar = new JButton("Registrar Cuenta");
        btnVolver = new JButton("<< Volver al Login");

        // Fila 1: Nombre
        panel.add(new JLabel("Nombre Completo:"));
        panel.add(txtNombre);
        
        // Fila 2: Email
        panel.add(new JLabel("Correo Electrónico:"));
        panel.add(txtEmail);
        
        // Fila 3: Contraseña
        panel.add(new JLabel("Contraseña (mín. 6 chars):"));
        panel.add(txtPassword);
        
        // Fila 4: Confirmar Contraseña
        panel.add(new JLabel("Confirmar Contraseña:"));
        panel.add(txtConfirmPassword);
        
        // Fila 5 y 6: Botones
        panel.add(btnVolver);
        panel.add(btnRegistrar);

        btnRegistrar.addActionListener(e -> intentarRegistro());
        
        btnVolver.addActionListener(e -> {
            this.dispose();
            loginFrame.setVisible(true); 
        });

        add(panel);
    }

    private void intentarRegistro() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // 1. Validaciones (SCE2.12)
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validación de formato de email simple (opcional, pero recomendado)
        if (!email.matches(".*@.*\\..*")) {
             JOptionPane.showMessageDialog(this, "Ingrese un formato de correo electrónico válido.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
             return;
        }

        try {
            // 2. Verificar si el email ya existe
            if (usuarioDAO.buscarPorEmail(email) != null) {
                JOptionPane.showMessageDialog(this, "El correo electrónico ya está registrado.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. Crear nuevo usuario (como Cliente)
            
            // Simulación del hash de contraseña antes de guardar
            String passHash = usuarioDAO.hashPasswordParaRegistro(password); 

            // Se crea el objeto Usuario con el hash y el rol "Cliente"
            Usuario nuevoUsuario = new Usuario(nombre, email, passHash, "Cliente");
            
            if (usuarioDAO.crear(nuevoUsuario)) {
                JOptionPane.showMessageDialog(this, "✅ Registro exitoso. Ya puedes iniciar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error desconocido al registrar el usuario.", "Error de DB", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
        }
    }
}