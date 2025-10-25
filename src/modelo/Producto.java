package modelo;

/**
 * Representa un producto en el sistema, mapeado a la tabla Producto en la DB.
 * Es la base para la gestión de catálogo y la lógica de venta.
 */
public class Producto {
    
    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock; // Cantidad disponible en inventario
    private String categoria;

    // --- Constructor Completo (usado para cargar desde DB) ---
    public Producto(int idProducto, String nombre, String descripcion, double precio, int stock, String categoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }
    
    // --- Constructor para Crear Nuevo Producto (ID autogenerado) ---
    public Producto(String nombre, String descripcion, double precio, int stock, String categoria) {
        // El ID se establecerá después de la inserción en la base de datos
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // -------------------------------------------------------------------
    // Getters y Setters (Necesarios para el Controlador y la GUI)
    // -------------------------------------------------------------------

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}