package modelo;

import java.util.HashMap;
import java.util.Map;

public class Carrito {
    // Clave: ID del Producto, Valor: Cantidad deseada
    private final Map<Integer, Integer> items; 
    
    public Carrito() {
        this.items = new HashMap<>();
    }
    
    // Método funcional para añadir productos al carrito
    public void agregarItem(Producto producto, int cantidad) {
        if (cantidad > 0) {
            items.put(producto.getIdProducto(), 
                      items.getOrDefault(producto.getIdProducto(), 0) + cantidad);
        }
    }
    
    // Método funcional para obtener el total (necesita el precio de los productos)
    public double calcularTotal(Map<Integer, Producto> productosDisponibles) {
        double total = 0.0;
        for (Map.Entry<Integer, Integer> item : items.entrySet()) {
            Producto p = productosDisponibles.get(item.getKey());
            if (p != null) {
                total += p.getPrecio() * item.getValue();
            }
        }
        return total;
    }
    
    // Obtener los items para procesar el pedido
    public Map<Integer, Integer> getItems() {
        return items;
    }
    
    public void vaciar() {
        items.clear();
    }
}