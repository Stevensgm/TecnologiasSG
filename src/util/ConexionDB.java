package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static final String URL = "jdbc:sqlite:tecnologias_sg.db"; // Nombre de la base de datos
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                System.out.println("✅ Conexión a la base de datos establecida exitosamente.");

                initializeDB(); 
                
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Error: Driver de SQLite no encontrado.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Error al conectar o inicializar la DB: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("✅ Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    private static void initializeDB() {
        if (connection == null) return;

        try (Statement stmt = connection.createStatement()) {
            
            // 1. Tabla Usuario (con password_hash)
            String sqlUsuario = "CREATE TABLE IF NOT EXISTS Usuario ("
                    + "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL,"
                    + "email TEXT UNIQUE NOT NULL,"
                    + "password_hash TEXT," 
                    + "rol TEXT NOT NULL" // Cliente o Administrador
                    + ");";
            stmt.execute(sqlUsuario);

            // 2. Tabla Producto (Componentes de PC)
            String sqlProducto = "CREATE TABLE IF NOT EXISTS Producto ("
                    + "idProducto INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL,"
                    + "descripcion TEXT,"
                    + "precio REAL NOT NULL,"
                    + "stock INTEGER NOT NULL,"
                    + "categoria TEXT" // Ej: CPU, GPU, RAM
                    + ");";
            stmt.execute(sqlProducto);

            // 3. Tabla Pedido
            String sqlPedido = "CREATE TABLE IF NOT EXISTS Pedido ("
                    + "idPedido INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "idUsuario INTEGER NOT NULL,"
                    + "fechaPedido TEXT NOT NULL,"
                    + "total REAL NOT NULL,"
                    + "FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)"
                    + ");";
            stmt.execute(sqlPedido);

            // 4. Tabla DetallePedido
            String sqlDetalle = "CREATE TABLE IF NOT EXISTS DetallePedido ("
                    + "idDetalle INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "idPedido INTEGER NOT NULL,"
                    + "idProducto INTEGER NOT NULL,"
                    + "cantidad INTEGER NOT NULL,"
                    + "precioUnitario REAL NOT NULL,"
                    + "FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido),"
                    + "FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)"
                    + ");";
            stmt.execute(sqlDetalle);
            
            System.out.println("✅ Estructura de tablas verificada/creada.");

        } catch (SQLException e) {
            System.err.println("❌ Error al crear tablas: " + e.getMessage());
        }
    }
}