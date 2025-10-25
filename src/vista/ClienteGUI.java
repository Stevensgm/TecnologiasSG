package vista;

import controlador.ProductoController;
import controlador.VentaController;
import modelo.Producto;
import modelo.DetallePedido;
import modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClienteGUI extends JFrame {

    private final ProductoController productoController;
    private final VentaController ventaController;
    // El ID de usuario debe pasarse de LoginGUI para un proyecto real. Usamos 2 (Cliente) por ahora.
    private final int ID_USUARIO_ACTUAL = 2; 

    private JTable tablaProductos;
    private JTable tablaCarrito;
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloCarrito;
    private Map<Integer, Producto> catalogoProductos; // Map<ID, Producto>
    
    private List<DetallePedido> carritoActual; // Lista de artículos en el carrito
    private JLabel lblTotal;
    private JTextField txtCantidad;
    private JButton btnAgregar;
    private JButton btnPagar;
    private JButton btnCerrarSesion;

    public ClienteGUI(ProductoController productoController, VentaController ventaController) {
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.carritoActual = new ArrayList<>();
        
        setTitle("Tecnología SG - Catálogo de Componentes de PC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        cargarProductos();
        initComponents();
        actualizarTotal();
    }

    private void cargarProductos() {
        // Cargar el catálogo de componentes de PC
        catalogoProductos = productoController.obtenerCatalogoMap();
        if (catalogoProductos == null) {
            catalogoProductos = new HashMap<>();
            JOptionPane.showMessageDialog(this, "No se pudo cargar el catálogo de productos.", "Error de Catálogo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // --- Paneles Principales ---
        JPanel panelCatalogo = crearPanelCatalogo();
        JPanel panelCarrito = crearPanelCarrito();
        JPanel panelSur = crearPanelSur();

        // Dividir la ventana entre Catálogo (arriba) y Carrito/Acciones (abajo)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelCatalogo, panelCarrito);
        splitPane.setDividerLocation(350); // Divide la pantalla aproximadamente a la mitad

        add(splitPane, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Componentes de PC disponibles"));

        // Definición de las columnas de la tabla de productos
        String[] columnas = {"ID", "Nombre", "Descripción", "Categoría", "Precio", "Stock"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Las celdas del catálogo no se pueden editar
            }
        };
        tablaProductos = new JTable(modeloProductos);
        
        cargarDatosProductos();

        // Panel de acciones para añadir al carrito
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtCantidad = new JTextField("1", 5);
        btnAgregar = new JButton("➕ Agregar al Carrito");
        
        panelAcciones.add(new JLabel("Cantidad:"));
        panelAcciones.add(txtCantidad);
        panelAcciones.add(btnAgregar);
        
        // Listener del botón Agregar
        btnAgregar.addActionListener(this::manejarAgregarProducto);

        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));

        // Definición de las columnas de la tabla del carrito
        String[] columnasCarrito = {"ID Prod.", "Nombre", "Cantidad", "Precio Unitario", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnasCarrito, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaCarrito = new JTable(modeloCarrito);

        panel.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelSur() {
        JPanel panelSur = new JPanel(new BorderLayout());
        
        // Panel de Total y Pagar
        JPanel panelTotalPagar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        lblTotal = new JLabel("Total a Pagar: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        btnPagar = new JButton("💳 Finalizar Compra");
        btnPagar.setEnabled(false); // Deshabilitado hasta que haya productos en el carrito
        
        btnPagar.addActionListener(this::manejarPagar);
        
        panelTotalPagar.add(lblTotal);
        panelTotalPagar.add(btnPagar);
        
        // Panel de Cerrar Sesión
        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        btnCerrarSesion = new JButton("Salir / Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        panelSur.add(panelCerrar, BorderLayout.WEST);
        panelSur.add(panelTotalPagar, BorderLayout.EAST);
        
        return panelSur;
    }

    private void cargarDatosProductos() {
        modeloProductos.setRowCount(0); // Limpiar tabla
        if (catalogoProductos == null) return;
        
        for (Producto p : catalogoProductos.values()) {
            modeloProductos.addRow(new Object[]{
                p.getIdProducto(), 
                p.getNombre(), 
                p.getDescripcion(), 
                p.getCategoria(), 
                String.format("%,.2f", p.getPrecio()), 
                p.getStock()
            });
        }
    }
    
    // --- Lógica del Carrito ---

    private void manejarAgregarProducto(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del catálogo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int idProducto = (int) modeloProductos.getValueAt(filaSeleccionada, 0);
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto producto = catalogoProductos.get(idProducto);
            if (producto.getStock() < cantidad) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente. Solo quedan " + producto.getStock() + " unidades.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            agregarAlCarrito(producto, cantidad);
            txtCantidad.setText("1"); // Resetear cantidad
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarAlCarrito(Producto producto, int cantidad) {
        // Buscar si el producto ya está en el carrito (simplificación: no busca el mismo producto)
        // Por ahora, simplemente añadimos una nueva línea en el carrito.
        
        DetallePedido nuevoDetalle = new DetallePedido(producto.getIdProducto(), cantidad, producto.getPrecio());
        nuevoDetalle.setProducto(producto);
        carritoActual.add(nuevoDetalle);
        
        actualizarTablaCarrito();
        actualizarTotal();
    }
    
    private void actualizarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        double totalGlobal = 0.0;
        
        for (DetallePedido dp : carritoActual) {
            Producto p = dp.getProducto();
            double subtotal = dp.getCantidad() * dp.getPrecioUnitario();
            totalGlobal += subtotal;
            
            modeloCarrito.addRow(new Object[]{
                p.getIdProducto(),
                p.getNombre(),
                dp.getCantidad(),
                String.format("%,.2f", dp.getPrecioUnitario()),
                String.format("%,.2f", subtotal)
            });
        }
        actualizarTotalLabel(totalGlobal);
    }
    
    private void actualizarTotal() {
        double totalGlobal = 0.0;
        for (DetallePedido dp : carritoActual) {
            totalGlobal += dp.getCantidad() * dp.getPrecioUnitario();
        }
        actualizarTotalLabel(totalGlobal);
        
        // Habilitar/deshabilitar el botón de pagar
        btnPagar.setEnabled(totalGlobal > 0);
    }
    
    private void actualizarTotalLabel(double total) {
        lblTotal.setText(String.format("Total a Pagar: $%,.2f", total));
    }
    
    // --- Lógica de Venta ---
    
    private void manejarPagar(java.awt.event.ActionEvent evt) {
        if (carritoActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double totalPedido = 0.0;
        for (DetallePedido dp : carritoActual) {
            totalPedido += dp.getCantidad() * dp.getPrecioUnitario();
        }

        // Crear el objeto Pedido
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Pedido nuevoPedido = new Pedido(ID_USUARIO_ACTUAL, fechaActual, totalPedido);
        nuevoPedido.setDetalles(carritoActual);
        
        // Procesar la venta
        if (ventaController.procesarVenta(nuevoPedido)) {
            JOptionPane.showMessageDialog(this, 
                "✅ ¡Compra realizada con éxito!\nTotal: $" + String.format("%,.2f", totalPedido), 
                "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar y actualizar
            carritoActual.clear();
            actualizarTablaCarrito();
            actualizarTotal();
            cargarDatosProductos(); // Recargar catálogo si el stock se actualizó (no implementado aún)
            
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al guardar el pedido en la base de datos.", "Error de Venta", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cerrarSesion() {
        this.dispose(); // Cierra la ventana actual
        // Aquí se debería crear una nueva instancia de LoginGUI, 
        // pero por simplicidad solo se cierra la ventana.
        System.exit(0); // Terminar el programa por ahora
    }
}