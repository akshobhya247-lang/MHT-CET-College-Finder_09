
package com.cet.finder.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CsvImportTask {

    public static int importCities(Path csvPath)
            throws IOException, SQLException {

        String sql = """
                INSERT INTO cities (city_name)
                VALUES (?)
                ON DUPLICATE KEY UPDATE
                city_name = VALUES(city_name)
                """;

        int imported = 0;

        try (Connection connection = DBConnection.getConnection();
             BufferedReader reader = Files.newBufferedReader(
                     csvPath, StandardCharsets.UTF_8);
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            String header = reader.readLine();

            if (header == null ||
                    !header.trim().equalsIgnoreCase("city_name")) {
                throw new IOException(
                        "CSV must have one header: city_name");
            }

            connection.setAutoCommit(false);

            try {
                String line;

                while ((line = reader.readLine()) != null) {
                    String cityName = line.trim();

                    if (cityName.isEmpty()) {
                        continue;
                    }

                    statement.setString(1, cityName);
                    statement.addBatch();
                }

                int[] results = statement.executeBatch();

                for (int result : results) {
                    if (result != java.sql.Statement.EXECUTE_FAILED) {
                        imported++;
                    }
                }

                connection.commit();

            } catch (SQLException | IOException e) {
                connection.rollback();
                throw e;
            }
        }

        return imported;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(
                    "Provide the path to a cities CSV file as an argument.");
            return;
        }

        try {
            int count = importCities(Path.of(args[0]));
            System.out.println("City rows processed: " + count);
        } catch (IOException | SQLException e) {
            System.out.println("CSV import failed: " + e.getMessage());
        }
    }
}
