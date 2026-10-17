
package com.cet.finder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/college_info_db";

    private static final String USER = "root";

    // Replace with your actual MySQL password
    private static final String PASSWORD = "Luz@2013";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("MySQL connected successfully!");
            System.out.println("Database: " +
                    connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
