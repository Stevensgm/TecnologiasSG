package vista;

import controlador.ProductoController;
import controlador.VentaController;
import dao.ProcedimientosAlmacenados;
import util.ConexionDB;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.Map;

/**
 * Clase principal de la aplicación Tecnología SG.
 * Punto de entrada del sistema de gestión de ventas.
 */
public class MainApp {
    
    public static void main(String[] args) {
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        TECNOLOGÍA SG - SISTEMA DE GESTIÓN DE VENTAS       ║");
        System.out.println("║              Proyecto Integrador - Cuarto Semestre         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            // Configurar Look and Feel nativo del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("✅ Look and Feel del sistema configurado correctamente.");
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo configurar el Look and Feel del sistema. Usando el predeterminado.");
        }
        
        System.out.println("\n--- INICIALIZACIÓN DEL SISTEMA ---\n");
        
        // Inicializar Controladores (esto también fuerza la conexión a la DB)
        System.out.println("📦 Inicializando controladores...");
        ProductoController productoCtrl = new ProductoController();
        VentaController ventaCtrl = new VentaController();
        System.out.println("✅ Controladores inicializados correctamente.\n");
        
        // Mostrar información de usuarios de prueba
        mostrarCredencialesPrueba();
        
        // Demostrar funcionalidad de Procedimientos Almacenados (SCE2.19)
        demostrarProcedimientosAlmacenados();
        
        // Ejecutar la interfaz gráfica de Login
        System.out.println("\n--- INICIANDO INTERFAZ GRÁFICA ---\n");
        SwingUtilities.invokeLater(() -> {
            try {
                LoginGUI loginGUI = new LoginGUI(productoCtrl, ventaCtrl);
                loginGUI.setVisible(true);
                System.out.println("✅ Ventana de Login mostrada correctamente.");
                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║  La aplicación está lista. Use la interfaz gráfica.       ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝\n");
                
            } catch (Exception e) {
                System.err.println("❌ ERROR CRÍTICO al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
                ConexionDB.closeConnection();
                System.exit(1); // Terminar si no se puede iniciar la GUI
            }
        });
    }
    
    /**
     * Muestra las credenciales de prueba para acceder al sistema.
     */
    private static void mostrarCredencialesPrueba() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              CREDENCIALES DE ACCESO DE PRUEBA              ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  👤 ADMINISTRADOR:                                         ║");
        System.out.println("║     Email:    admin@sg.com                                 ║");
        System.out.println("║     Password: 123456                                       ║");
        System.out.println("║                                                            ║");
        System.out.println("║  🛒 CLIENTE:                                               ║");
        System.out.println("║     Email:    cliente.prueba@sg.com                        ║");
        System.out.println("║     Password: 123456                                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Demuestra la funcionalidad de los Procedimientos Almacenados (SCE2.19).
     * Esta es una prueba opcional que se ejecuta al iniciar la aplicación.
     */
    private static void demostrarProcedimientosAlmacenados() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     DEMOSTRACIÓN DE PROCEDIMIENTOS ALMACENADOS (SCE2.19)   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            ProcedimientosAlmacenados sp = new ProcedimientosAlmacenados();
            
            // Procedimiento 1: Ventas por categoría
            System.out.println("📊 Ejecutando: obtenerVentasPorCategoria()");
            Map<String, Double> ventasPorCategoria = sp.obtenerVentasPorCategoria();
            if (ventasPorCategoria.isEmpty()) {
                System.out.println("   ℹ️  No hay ventas registradas aún.");
            } else {
                ventasPorCategoria.forEach((categoria, total) -> 
                    System.out.println("   • " + categoria + ": $" + String.format("%,.2f", total))
                );
            }
            System.out.println();
            
            // Procedimiento 2: Productos con stock bajo
            System.out.println("⚠️  Ejecutando: obtenerProductosConStockBajo(10)");
            Map<Integer, Integer> stockBajo = sp.obtenerProductosConStockBajo(10);
            if (stockBajo.isEmpty()) {
                System.out.println("   ✅ Todos los productos tienen stock suficiente.");
            } else {
                System.out.println("   ⚠️  Se encontraron " + stockBajo.size() + " productos con stock bajo.");
            }
            System.out.println();
            
            // Procedimiento 3: Total de ventas de un cliente
            System.out.println("💰 Ejecutando: obtenerTotalVentasCliente(2)");
            double totalCliente = sp.obtenerTotalVentasCliente(2);
            System.out.println("   Cliente ID 2 ha gastado: $" + String.format("%,.2f", totalCliente));
            System.out.println();
            
            // Procedimiento 4: Reporte de ventas por período
            System.out.println("📈 Ejecutando: obtenerReporteVentasPorPeriodo()");
            String fechaInicio = "2024-01-01";
            String fechaFin = "2024-12-31";
            Map<String, Object> reporte = sp.obtenerReporteVentasPorPeriodo(fechaInicio, fechaFin);
            if (reporte.isEmpty() || (int)reporte.get("cantidadPedidos") == 0) {
                System.out.println("   ℹ️  No hay ventas en el período " + fechaInicio + " a " + fechaFin);
            } else {
                System.out.println("   📅 Período: " + fechaInicio + " a " + fechaFin);
                System.out.println("   • Cantidad de pedidos: " + reporte.get("cantidadPedidos"));
                System.out.println("   • Total ventas: $" + String.format("%,.2f", reporte.get("totalVentas")));
                System.out.println("   • Promedio por venta: $" + String.format("%,.2f", reporte.get("promedioVenta")));
                System.out.println("   • Venta mayor: $" + String.format("%,.2f", reporte.get("ventaMayor")));
                System.out.println("   • Venta menor: $" + String.format("%,.2f", reporte.get("ventaMenor")));
            }
            System.out.println();
            
            // Procedimiento 6: Top productos más vendidos
            System.out.println("🏆 Ejecutando: obtenerTopProductosMasVendidos(5)");
            Map<Integer, Integer> topProductos = sp.obtenerTopProductosMasVendidos(5);
            if (topProductos.isEmpty()) {
                System.out.println("   ℹ️  No hay productos vendidos aún.");
            }
            System.out.println();
            
            System.out.println("✅ Demostración de Procedimientos Almacenados completada.");
            System.out.println("   Nota: Estos procedimientos están disponibles para reportes y análisis.");
            
        } catch (Exception e) {
            System.err.println("⚠️  Error al ejecutar demostración de procedimientos almacenados:");
            System.err.println("   " + e.getMessage());
            System.err.println("   (Esto es normal si la base de datos está vacía)");
        }
        
        System.out.println();
    }
    
    /**
     * Hook para manejar el cierre de la aplicación.
     * Se ejecuta cuando se cierra la última ventana o se termina el programa.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n--- CERRANDO APLICACIÓN ---");
            System.out.println("🔌 Cerrando conexión a la base de datos...");
            ConexionDB.closeConnection();
            System.out.println("👋 Aplicación cerrada correctamente. ¡Hasta pronto!");
        }));
    }
}