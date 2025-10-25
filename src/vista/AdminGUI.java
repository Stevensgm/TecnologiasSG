package vista;

import controlador.ProductoController;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminGUI extends JFrame {

    private final ProductoController productoController;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtPrecio, txtStock, txtCategoria, txtDescripcion;
    private JTextField txtIdProductoStock, txtCantidadStock;
    private JButton btnActualizarStock, btnCrearProducto, btnRefrescar;

    public AdminGUI(ProductoController productoController) {
        this.productoController = productoController;
        setTitle("Tecnologia SG - Catálogo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Usamos BorderLayout para estructura principal
        
        // Inicializar componentes
        initComponents();
        
        // Cargar datos iniciales
        cargarProductosEnTabla();

        // Configuración final de la ventana
        setSize(1000, 600);
        setLocationRelativeTo(null); // Centrar en la pantalla
    }

    private void initComponents() {
        // --- Paneles Principales ---
        JPanel panelTabla = crearPanelTabla();
        JPanel panelControles = crearPanelControles();
        
        // Añadir paneles al frame principal
        add(panelTabla, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH); // Controles en la parte inferior
    }

    private JPanel crearPanelTabla() {
        // Definición del modelo y la tabla
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Stock", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos que la tabla no sea editable
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        
        // Panel contenedor de la tabla
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Catálogo de Productos"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botón de refrescar
        btnRefrescar = new JButton("Refrescar Catálogo");
        btnRefrescar.addActionListener(e -> cargarProductosEnTabla());
        JPanel panelBotonRefrescar = new JPanel();
        panelBotonRefrescar.add(btnRefrescar);
        panel.add(panelBotonRefrescar, BorderLayout.NORTH);

        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2, 10, 10)); // Dos columnas: Crear Producto y Stock
        
        // --- 1. Panel para CREAR PRODUCTO ---
        JPanel panelCrear = new JPanel(new GridBagLayout());
        panelCrear.setBorder(BorderFactory.createTitledBorder("Crear Nuevo Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Margen interno

        // Campos de texto para nuevo producto
        txtNombre = new JTextField(15);
        txtDescripcion = new JTextField(15);
        txtPrecio = new JTextField(15);
        txtStock = new JTextField(15);
        txtCategoria = new JTextField(15);
        btnCrearProducto = new JButton("Crear Producto");

        // Añadir componentes al panelCrear
        gbc.gridx = 0; gbc.gridy = 0; panelCrear.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panelCrear.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; panelCrear.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panelCrear.add(txtDescripcion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; panelCrear.add(new JLabel("Precio ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panelCrear.add(txtPrecio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; panelCrear.add(new JLabel("Stock Inicial:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panelCrear.add(txtStock, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; panelCrear.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; panelCrear.add(txtCategoria, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; panelCrear.add(btnCrearProducto, gbc);

        // --- 2. Panel para MODIFICAR STOCK ---
        JPanel panelStock = new JPanel(new GridBagLayout());
        panelStock.setBorder(BorderFactory.createTitledBorder("Modificar Stock Existente (Añadir/Reducir)"));
        
        txtIdProductoStock = new JTextField(15);
        txtCantidadStock = new JTextField(15);
        btnActualizarStock = new JButton("Aplicar Cambio de Stock");

        // Añadir componentes al panelStock
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; panelStock.add(new JLabel("ID Producto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panelStock.add(txtIdProductoStock, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelStock.add(new JLabel("Cantidad ( +/- ):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panelStock.add(txtCantidadStock, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panelStock.add(btnActualizarStock, gbc);

        // --- 3. Agregar Listeners a los botones ---
        btnCrearProducto.addActionListener(e -> crearProducto());
        btnActualizarStock.addActionListener(e -> actualizarStock());

        // --- 4. Añadir subpaneles al panel principal de controles ---
        panelPrincipal.add(panelCrear);
        panelPrincipal.add(panelStock);
        
        return panelPrincipal;
    }

    // -------------------------------------------------------------------
    // Lógica de Negocio y Persistencia (Llama al Controlador)
    // -------------------------------------------------------------------

    private void cargarProductosEnTabla() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        try {
            List<Producto> productos = productoController.obtenerCatalogo();
            for (Producto p : productos) {
                modeloTabla.addRow(new Object[]{
                    p.getIdProducto(), 
                    p.getNombre(), 
                    String.format("%.2f", p.getPrecio()), 
                    p.getStock(), 
                    p.getCategoria()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el catálogo: " + e.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void crearProducto() {
        try {
            // Validación de datos (SCE2.12)
            String nombre = txtNombre.getText();
            String descripcion = txtDescripcion.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            String categoria = txtCategoria.getText();
            
            // Llamar al Controlador
            String resultado = productoController.crearProducto(nombre, descripcion, precio, stock, categoria);
            
            // Mostrar resultado y refrescar
            if (resultado.startsWith("✅")) {
                JOptionPane.showMessageDialog(this, resultado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Limpiar campos
                txtNombre.setText("");
                txtDescripcion.setText("");
                txtPrecio.setText("");
                txtStock.setText("");
                txtCategoria.setText("");
                cargarProductosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Error de Validación/DB", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Precio y Stock deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarStock() {
        try {
            // Validación de datos (SCE2.12)
            int id = Integer.parseInt(txtIdProductoStock.getText());
            int cantidad = Integer.parseInt(txtCantidadStock.getText()); // Puede ser positivo (añadir) o negativo (reducir)
            
            // Llamar al Controlador
            String resultado = productoController.modificarStock(id, cantidad);
            
            // Mostrar resultado y refrescar
            if (resultado.startsWith("✅")) {
                JOptionPane.showMessageDialog(this, resultado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Error de Stock/DB", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: ID y Cantidad deben ser números enteros.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}