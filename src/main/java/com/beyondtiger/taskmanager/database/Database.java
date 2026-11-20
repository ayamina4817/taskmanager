package com.beyondtiger.taskmanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.File;

public class Database {

    private static final String DB_PATH =
            System.getProperty("user.home") + "/taskmanager.db";

    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {

                statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                statement.execute("""
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        status TEXT NOT NULL DEFAULT '未着手',
                        due_date DATE,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    )
                """);

                System.out.println("Database initialized successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
