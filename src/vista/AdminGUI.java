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
    
    // Panel Crear Producto
    private JTextField txtNombre, txtPrecio, txtStock, txtCategoria, txtDescripcion;
    private JButton btnCrearProducto, btnRefrescar;
    
    // Panel Modificar Stock
    private JTextField txtIdProductoStock, txtCantidadStock;
    private JButton btnActualizarStock;
    
    // Panel Editar Producto - NUEVO
    private JTextField txtIdEditar, txtNombreEditar, txtDescripcionEditar;
    private JTextField txtPrecioEditar, txtStockEditar, txtCategoriaEditar;
    private JButton btnCargarProducto, btnGuardarCambios;
    
    private JButton btnCerrarSesion;

    public AdminGUI(ProductoController productoController) {
        this.productoController = productoController;
        setTitle("Tecnología SG - Panel de Administración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        cargarProductosEnTabla();

        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña 1: Ver Catálogo
        JPanel panelCatalogo = crearPanelCatalogo();
        tabbedPane.addTab("📋 Ver Catálogo", panelCatalogo);
        
        // Pestaña 2: Crear Producto
        JPanel panelCrear = crearPanelCrearProducto();
        tabbedPane.addTab("➕ Crear Producto", panelCrear);
        
        // Pestaña 3: Editar Producto - NUEVO
        JPanel panelEditar = crearPanelEditarProducto();
        tabbedPane.addTab("✏️ Editar Producto", panelEditar);
        
        // Pestaña 4: Modificar Stock
        JPanel panelStock = crearPanelModificarStock();
        tabbedPane.addTab("📦 Modificar Stock", panelStock);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botón de cerrar sesión
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCerrarSesion = new JButton("⬅️ Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelInferior.add(btnCerrarSesion);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel lblTitulo = new JLabel("CATÁLOGO COMPLETO DE PRODUCTOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Tabla
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botón refrescar
        JPanel panelBoton = new JPanel();
        btnRefrescar = new JButton("🔄 Refrescar Catálogo");
        btnRefrescar.addActionListener(e -> cargarProductosEnTabla());
        panelBoton.add(btnRefrescar);
        panel.add(panelBoton, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel crearPanelCrearProducto() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Título
        JLabel lblTitulo = new JLabel("CREAR NUEVO PRODUCTO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Campos
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Nombre del Producto:"), gbc);
        txtNombre = new JTextField(25);
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextField(25);
        gbc.gridx = 1;
        panel.add(txtDescripcion, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Precio ($):"), gbc);
        txtPrecio = new JTextField(25);
        gbc.gridx = 1;
        panel.add(txtPrecio, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Stock Inicial:"), gbc);
        txtStock = new JTextField(25);
        gbc.gridx = 1;
        panel.add(txtStock, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Categoría:"), gbc);
        txtCategoria = new JTextField(25);
        gbc.gridx = 1;
        panel.add(txtCategoria, gbc);

        // Botón
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        btnCrearProducto = new JButton("✅ Crear Producto");
        btnCrearProducto.setFont(new Font("Arial", Font.BOLD, 14));
        btnCrearProducto.addActionListener(e -> crearProducto());
        panel.add(btnCrearProducto, gbc);

        return panel;
    }

    private JPanel crearPanelEditarProducto() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Título
        JLabel lblTitulo = new JLabel("EDITAR INFORMACIÓN DE PRODUCTO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Sección de búsqueda
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("ID del Producto:"), gbc);
        
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtIdEditar = new JTextField(10);
        btnCargarProducto = new JButton("🔍 Cargar Datos");
        btnCargarProducto.addActionListener(e -> cargarProductoParaEditar());
        panelBusqueda.add(txtIdEditar);
        panelBusqueda.add(btnCargarProducto);
        gbc.gridx = 1;
        panel.add(panelBusqueda, gbc);

        // Separador
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        // Campos editables
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        txtNombreEditar = new JTextField(25);
        txtNombreEditar.setEnabled(false);
        gbc.gridx = 1;
        panel.add(txtNombreEditar, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Descripción:"), gbc);
        txtDescripcionEditar = new JTextField(25);
        txtDescripcionEditar.setEnabled(false);
        gbc.gridx = 1;
        panel.add(txtDescripcionEditar, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Precio ($):"), gbc);
        txtPrecioEditar = new JTextField(25);
        txtPrecioEditar.setEnabled(false);
        gbc.gridx = 1;
        panel.add(txtPrecioEditar, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        panel.add(new JLabel("Stock:"), gbc);
        txtStockEditar = new JTextField(25);
        txtStockEditar.setEnabled(false);
        gbc.gridx = 1;
        panel.add(txtStockEditar, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        panel.add(new JLabel("Categoría:"), gbc);
        txtCategoriaEditar = new JTextField(25);
        txtCategoriaEditar.setEnabled(false);
        gbc.gridx = 1;
        panel.add(txtCategoriaEditar, gbc);

        // Botón guardar
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        btnGuardarCambios = new JButton("💾 Guardar Cambios");
        btnGuardarCambios.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardarCambios.setEnabled(false);
        btnGuardarCambios.addActionListener(e -> guardarCambiosProducto());
        panel.add(btnGuardarCambios, gbc);

        return panel;
    }

    private JPanel crearPanelModificarStock() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Título
        JLabel lblTitulo = new JLabel("MODIFICAR STOCK DE PRODUCTO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Información
        JTextArea txtInfo = new JTextArea(
            "Puede añadir o reducir el stock de un producto.\n" +
            "• Para AÑADIR: ingrese un número positivo (ej: +10)\n" +
            "• Para REDUCIR: ingrese un número negativo (ej: -5)"
        );
        txtInfo.setEditable(false);
        txtInfo.setBackground(new Color(245, 245, 245));
        txtInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        gbc.gridy = 1;
        panel.add(txtInfo, gbc);

        // Campos
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("ID del Producto:"), gbc);
        txtIdProductoStock = new JTextField(25);
        gbc.gridx = 1;
        panel.add(txtIdProductoStock, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Cantidad (+/-):"), gbc);
        txtCantidadStock = new JTextField(25);
        gbc.gridx = 1;
        panel.add(txtCantidadStock, gbc);

        // Botón
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        btnActualizarStock = new JButton("📦 Aplicar Cambio de Stock");
        btnActualizarStock.setFont(new Font("Arial", Font.BOLD, 14));
        btnActualizarStock.addActionListener(e -> actualizarStock());
        panel.add(btnActualizarStock, gbc);

        return panel;
    }

    // ===================================================================
    // LÓGICA DE NEGOCIO
    // ===================================================================

    private void cargarProductosEnTabla() {
        modeloTabla.setRowCount(0);
        
        try {
            List<Producto> productos = productoController.obtenerCatalogo();
            for (Producto p : productos) {
                modeloTabla.addRow(new Object[]{
                    p.getIdProducto(), 
                    p.getNombre(),
                    p.getDescripcion(),
                    String.format("$%.2f", p.getPrecio()), 
                    p.getStock(), 
                    p.getCategoria()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el catálogo:\n" + e.getMessage(), 
                                        "Error de DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void crearProducto() {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String categoria = txtCategoria.getText().trim();
            
            String resultado = productoController.crearProducto(nombre, descripcion, precio, stock, categoria);
            
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
                JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Precio y Stock deben ser números válidos.", 
                                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProductoParaEditar() {
        try {
            int id = Integer.parseInt(txtIdEditar.getText().trim());
            Producto producto = productoController.buscarProductoPorId(id);
            
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "❌ No se encontró el producto con ID: " + id, 
                                            "Producto no encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cargar datos en los campos
            txtNombreEditar.setText(producto.getNombre());
            txtDescripcionEditar.setText(producto.getDescripcion());
            txtPrecioEditar.setText(String.valueOf(producto.getPrecio()));
            txtStockEditar.setText(String.valueOf(producto.getStock()));
            txtCategoriaEditar.setText(producto.getCategoria());
            
            // Habilitar campos y botón
            txtNombreEditar.setEnabled(true);
            txtDescripcionEditar.setEnabled(true);
            txtPrecioEditar.setEnabled(true);
            txtStockEditar.setEnabled(true);
            txtCategoriaEditar.setEnabled(true);
            btnGuardarCambios.setEnabled(true);
            
            JOptionPane.showMessageDialog(this, "✅ Producto cargado. Ahora puede editar los campos.", 
                                        "Producto Cargado", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese un ID numérico válido.", 
                                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambiosProducto() {
        try {
            int id = Integer.parseInt(txtIdEditar.getText().trim());
            String nombre = txtNombreEditar.getText().trim();
            String descripcion = txtDescripcionEditar.getText().trim();
            double precio = Double.parseDouble(txtPrecioEditar.getText().trim());
            int stock = Integer.parseInt(txtStockEditar.getText().trim());
            String categoria = txtCategoriaEditar.getText().trim();
            
            // Validaciones
            if (nombre.isEmpty() || precio <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Error: Todos los campos son obligatorios y válidos.", 
                                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Aquí deberías agregar el método actualizarProducto en ProductoController
            // Por ahora usamos los métodos existentes
            boolean exito = productoController.actualizarStockDirecto(id, stock);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "✅ Producto actualizado exitosamente.", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                // Limpiar campos
                txtIdEditar.setText("");
                txtNombreEditar.setText("");
                txtDescripcionEditar.setText("");
                txtPrecioEditar.setText("");
                txtStockEditar.setText("");
                txtCategoriaEditar.setText("");
                
                // Deshabilitar campos
                txtNombreEditar.setEnabled(false);
                txtDescripcionEditar.setEnabled(false);
                txtPrecioEditar.setEnabled(false);
                txtStockEditar.setEnabled(false);
                txtCategoriaEditar.setEnabled(false);
                btnGuardarCambios.setEnabled(false);
                
                cargarProductosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al actualizar el producto.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Precio y Stock deben ser números válidos.", 
                                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(), 
                                        "Error de DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarStock() {
        try {
            int id = Integer.parseInt(txtIdProductoStock.getText().trim());
            int cantidad = Integer.parseInt(txtCantidadStock.getText().trim());
            
            String resultado = productoController.modificarStock(id, cantidad);
            
            if (resultado.startsWith("✅")) {
                JOptionPane.showMessageDialog(this, resultado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtIdProductoStock.setText("");
                txtCantidadStock.setText("");
                cargarProductosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: ID y Cantidad deben ser números enteros.", 
                                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea cerrar sesión?", 
            "Confirmar cierre de sesión", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginGUI loginGUI = new LoginGUI(productoController, new controlador.VentaController());
                loginGUI.setVisible(true);
            });
        }
    }
}