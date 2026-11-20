package com.beyondtiger.taskmanager.dao;

import com.beyondtiger.taskmanager.database.Database;
import com.beyondtiger.taskmanager.model.User;
import com.beyondtiger.taskmanager.service.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean create(User user) throws Exception {

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        String hashedPassword = PasswordUtil.hash(user.getPassword());

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, hashedPassword);

            return statement.executeUpdate() == 1;
        }
    }

    public User findByUsername(String username) throws Exception {

        String sql = "SELECT id, username, password FROM users WHERE username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                    );
                }
            }
        }

        return null;
    }
}
