package vista;

import controlador.ProductoController;
import controlador.VentaController;
import modelo.Producto;
import modelo.DetallePedido;
import modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClienteGUI extends JFrame {

    private final ProductoController productoController;
    private final VentaController ventaController;
    private final int ID_USUARIO_ACTUAL; // Ahora lo recibimos como parámetro

    private JTable tablaProductos;
    private JTable tablaCarrito;
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloCarrito;
    private Map<Integer, Producto> catalogoProductos;
    
    private List<DetallePedido> carritoActual;
    private JLabel lblTotal;
    private JTextField txtCantidad;
    private JButton btnAgregar;
    private JButton btnPagar;
    private JButton btnVaciarCarrito;
    private JButton btnEliminarItem;
    private JButton btnCerrarSesion;
    private JButton btnRefrescarCatalogo;

    // Constructor actualizado para recibir el ID del usuario logueado
    public ClienteGUI(ProductoController productoController, VentaController ventaController, int idUsuario) {
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.ID_USUARIO_ACTUAL = idUsuario;
        this.carritoActual = new ArrayList<>();
        
        setTitle("Tecnología SG - Tienda de Componentes de PC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        cargarProductos();
        initComponents();
        actualizarTotal();
    }

    private void cargarProductos() {
        try {
            catalogoProductos = productoController.obtenerCatalogoMap();
            if (catalogoProductos == null) {
                catalogoProductos = new HashMap<>();
                JOptionPane.showMessageDialog(this, "No se pudo cargar el catálogo de productos.", "Error de Catálogo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            catalogoProductos = new HashMap<>();
            JOptionPane.showMessageDialog(this, "Error de base de datos al cargar productos: " + e.getMessage(), 
                                        "Error de DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelCatalogo = crearPanelCatalogo();
        JPanel panelCarrito = crearPanelCarrito();
        JPanel panelSur = crearPanelSur();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelCatalogo, panelCarrito);
        splitPane.setDividerLocation(350);

        add(splitPane, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Componentes de PC disponibles"));

        String[] columnas = {"ID", "Nombre", "Descripción", "Categoría", "Precio", "Stock"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloProductos);
        
        cargarDatosProductos();

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtCantidad = new JTextField("1", 5);
        btnAgregar = new JButton("➕ Agregar al Carrito");
        btnRefrescarCatalogo = new JButton("🔄 Refrescar Catálogo");
        
        panelAcciones.add(new JLabel("Cantidad:"));
        panelAcciones.add(txtCantidad);
        panelAcciones.add(btnAgregar);
        panelAcciones.add(btnRefrescarCatalogo);
        
        btnAgregar.addActionListener(e -> manejarAgregarProducto());
        btnRefrescarCatalogo.addActionListener(e -> {
            cargarProductos();
            cargarDatosProductos();
            JOptionPane.showMessageDialog(this, "✅ Catálogo actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));

        String[] columnasCarrito = {"ID Prod.", "Nombre", "Cantidad", "Precio Unitario", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnasCarrito, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaCarrito = new JTable(modeloCarrito);

        // Panel de botones para el carrito
        JPanel panelBotonesCarrito = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnVaciarCarrito = new JButton("🗑️ Vaciar Carrito");
        btnEliminarItem = new JButton("❌ Eliminar Item Seleccionado");
        
        btnVaciarCarrito.addActionListener(e -> vaciarCarrito());
        btnEliminarItem.addActionListener(e -> eliminarItemSeleccionado());
        
        panelBotonesCarrito.add(btnEliminarItem);
        panelBotonesCarrito.add(btnVaciarCarrito);

        panel.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);
        panel.add(panelBotonesCarrito, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelSur() {
        JPanel panelSur = new JPanel(new BorderLayout());
        
        JPanel panelTotalPagar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        lblTotal = new JLabel("Total a Pagar: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        btnPagar = new JButton("💳 Finalizar Compra");
        btnPagar.setEnabled(false);
        
        btnPagar.addActionListener(e -> manejarPagar());
        
        panelTotalPagar.add(lblTotal);
        panelTotalPagar.add(btnPagar);
        
        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        btnCerrarSesion = new JButton("⬅️ Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        panelSur.add(panelCerrar, BorderLayout.WEST);
        panelSur.add(panelTotalPagar, BorderLayout.EAST);
        
        return panelSur;
    }

    private void cargarDatosProductos() {
        modeloProductos.setRowCount(0);
        if (catalogoProductos == null || catalogoProductos.isEmpty()) return;
        
        for (Producto p : catalogoProductos.values()) {
            modeloProductos.addRow(new Object[]{
                p.getIdProducto(), 
                p.getNombre(), 
                p.getDescripcion(), 
                p.getCategoria(), 
                String.format("$%,.2f", p.getPrecio()), 
                p.getStock()
            });
        }
    }

    private void manejarAgregarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto del catálogo.", 
                                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idProducto = (int) modeloProductos.getValueAt(filaSeleccionada, 0);
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", 
                                            "Cantidad inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto producto = catalogoProductos.get(idProducto);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Error al obtener información del producto.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (producto.getStock() < cantidad) {
                JOptionPane.showMessageDialog(this, 
                    "Stock insuficiente. Solo quedan " + producto.getStock() + " unidades disponibles.", 
                    "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            agregarAlCarrito(producto, cantidad);
            txtCantidad.setText("1");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una cantidad numérica válida.", 
                                        "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarAlCarrito(Producto producto, int cantidad) {
        DetallePedido nuevoDetalle = new DetallePedido(producto.getIdProducto(), cantidad, producto.getPrecio());
        nuevoDetalle.setProducto(producto);
        carritoActual.add(nuevoDetalle);
        
        actualizarTablaCarrito();
        actualizarTotal();
        
        JOptionPane.showMessageDialog(this, 
            "✅ " + cantidad + " x " + producto.getNombre() + " agregado(s) al carrito", 
            "Producto agregado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void actualizarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        
        for (DetallePedido dp : carritoActual) {
            Producto p = dp.getProducto();
            double subtotal = dp.getCantidad() * dp.getPrecioUnitario();
            
            modeloCarrito.addRow(new Object[]{
                p.getIdProducto(),
                p.getNombre(),
                dp.getCantidad(),
                String.format("$%,.2f", dp.getPrecioUnitario()),
                String.format("$%,.2f", subtotal)
            });
        }
    }
    
    private void actualizarTotal() {
        double totalGlobal = 0.0;
        for (DetallePedido dp : carritoActual) {
            totalGlobal += dp.getCantidad() * dp.getPrecioUnitario();
        }
        actualizarTotalLabel(totalGlobal);
        btnPagar.setEnabled(totalGlobal > 0);
    }
    
    private void actualizarTotalLabel(double total) {
        lblTotal.setText(String.format("Total a Pagar: $%,.2f", total));
    }
    
    private void vaciarCarrito() {
        if (carritoActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito ya está vacío.", 
                                        "Carrito vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea vaciar el carrito?", 
            "Confirmar acción", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            carritoActual.clear();
            actualizarTablaCarrito();
            actualizarTotal();
            JOptionPane.showMessageDialog(this, "🗑️ Carrito vaciado", 
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void eliminarItemSeleccionado() {
        int filaSeleccionada = tablaCarrito.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un item del carrito.", 
                                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        carritoActual.remove(filaSeleccionada);
        actualizarTablaCarrito();
        actualizarTotal();
        JOptionPane.showMessageDialog(this, "✅ Item eliminado del carrito", 
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void manejarPagar() {
        if (carritoActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío. No hay nada que comprar.", 
                                        "Carrito vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double totalPedido = 0.0;
        for (DetallePedido dp : carritoActual) {
            totalPedido += dp.getCantidad() * dp.getPrecioUnitario();
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Confirma la compra por un total de $" + String.format("%,.2f", totalPedido) + "?", 
            "Confirmar compra", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Pedido nuevoPedido = new Pedido(ID_USUARIO_ACTUAL, fechaActual, totalPedido);
        nuevoPedido.setDetalles(carritoActual);
        
        // CORRECCIÓN CRÍTICA: ahora procesarVenta() reduce automáticamente el stock
        if (ventaController.procesarVenta(nuevoPedido)) {
            JOptionPane.showMessageDialog(this, 
                "✅ ¡Compra realizada con éxito!\n\n" +
                "Pedido ID: " + nuevoPedido.getIdPedido() + "\n" +
                "Total pagado: $" + String.format("%,.2f", totalPedido) + "\n\n" +
                "¡Gracias por su compra!", 
                "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar carrito y actualizar catálogo
            carritoActual.clear();
            actualizarTablaCarrito();
            actualizarTotal();
            
            // IMPORTANTE: Recargar el catálogo para mostrar el stock actualizado
            cargarProductos();
            cargarDatosProductos();
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ Error al procesar la compra.\n" +
                "Puede ser por stock insuficiente o un problema con la base de datos.\n" +
                "Por favor, intente nuevamente o contacte al administrador.", 
                "Error de Venta", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea cerrar sesión?", 
            "Confirmar cierre de sesión", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginGUI(productoController, ventaController).setVisible(true);
        }
    }
}