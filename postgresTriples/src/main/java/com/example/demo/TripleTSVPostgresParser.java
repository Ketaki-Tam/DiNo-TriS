package com.example.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TripleTSVPostgresParser {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/yago_test"; // Replace with your database connection URL
    private static final String USER = "<username>"; // Replace with your database username
    private static final String PASS = "<password>"; // Replace with your database password

    public static void main(String[] args) {
        String filePath = "your/tsv/file/path"; // Replace with your TSV file path containing the triples

        Connection connection = null;
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            // Prepare a statement for inserting triples
            String sql = "INSERT INTO triple (subject, predicate, object) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Read the TSV file line by line
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the line and remove leading/trailing '<' and '>'
                String[] parts = line.trim().split(" ");
                if(parts.length >= 3)
                {
                    String subject = parts[0].replaceAll("^<|>$", "");
                    String predicate = parts[1].replaceAll("^<|>$", "");
                    String object = parts[2].replaceAll("^<|>$", "");

                    preparedStatement.setString(1, subject);
                    preparedStatement.setString(2, predicate);
                    preparedStatement.setString(3, object);

                    // Add the triple to the database
                    preparedStatement.addBatch();
                }

            }

            // Execute the batch insert for efficiency
            preparedStatement.executeBatch();

            reader.close();
            System.out.println("Triples successfully inserted from TSV file.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}