package vista;

import controlador.ProductoController;
import controlador.VentaController;
import util.ConexionDB;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        
        // Inicializar Controladores para forzar la conexión y la carga inicial del catálogo
        ProductoController productoCtrl = new ProductoController();
        VentaController ventaCtrl = new VentaController();

        // Ejecutar la interfaz gráfica de Login
        SwingUtilities.invokeLater(() -> {
            try {
                // Lanzar directamente la ventana de Login
                new LoginGUI(productoCtrl, ventaCtrl).setVisible(true);
            } catch (Exception e) {
                System.err.println("❌ ERROR al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
                ConexionDB.closeConnection(); 
            }
        });
        
        // Se necesita crear un usuario Administrador y Cliente de prueba en la DB para que el login funcione.
        // Esto se puede hacer en la inicialización de UsuarioDAOImpl.
    }
}