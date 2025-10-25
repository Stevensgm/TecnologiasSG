package modelo;

import java.util.List;

public class Pedido {
    private int idPedido;
    private int idUsuario;
    private String fechaPedido; // Formato TEXT en DB (ej: YYYY-MM-DD HH:MM:SS)
    private double total;
    
    // Lista para contener los productos y cantidades del pedido
    private List<DetallePedido> detalles; 

    // Constructor completo
    public Pedido(int idPedido, int idUsuario, String fechaPedido, double total) {
        this.idPedido = idPedido;
        this.idUsuario = idUsuario;
        this.fechaPedido = fechaPedido;
        this.total = total;
    }

    // Constructor para crear un nuevo pedido (ID se genera en DB)
    public Pedido(int idUsuario, String fechaPedido, double total) {
        this.idUsuario = idUsuario;
        this.fechaPedido = fechaPedido;
        this.total = total;
    }
    
    // --- Getters y Setters ---
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(String fechaPedido) { this.fechaPedido = fechaPedido; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
}