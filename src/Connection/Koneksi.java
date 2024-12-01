package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
  private static Koneksi instance = new Koneksi();
  private static Connection conn; 

  public static Koneksi getInstance() {
    return instance;
  }

  public static Connection getConnection() {
    if (conn == null) { // Buat koneksi jika belum ada
      try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
          "jdbc:mysql://localhost:3306/tas",
          "root",
          ""
        );
      } catch (ClassNotFoundException | SQLException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
    return conn;
  }
}
