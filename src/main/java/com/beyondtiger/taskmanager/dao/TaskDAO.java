package com.beyondtiger.taskmanager.dao;

import com.beyondtiger.taskmanager.database.Database;
import com.beyondtiger.taskmanager.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public boolean create(Task task) throws Exception {

        String sql = """
            INSERT INTO tasks (user_id, title, description, status, due_date)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, task.getUserId());
            statement.setString(2, task.getTitle());
            statement.setString(3, task.getDescription());
            statement.setString(4, task.getStatus());
            statement.setString(5, task.getDueDate());

            return statement.executeUpdate() == 1;
        }
    }

    public List<Task> findAllByUserId(int userId) throws Exception {

        List<Task> tasks = new ArrayList<>();

        String sql = """
            SELECT id, user_id, title, description, status, due_date
            FROM tasks
            WHERE user_id = ?
            ORDER BY id DESC
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    tasks.add(new Task(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("due_date")
                    ));
                }
            }
        }

        return tasks;
    }

    public Task findByIdAndUserId(int id, int userId) throws Exception {

        String sql = """
            SELECT id, user_id, title, description, status, due_date
            FROM tasks
            WHERE id = ? AND user_id = ?
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return new Task(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("due_date")
                    );
                }
            }
        }

        return null;
    }

    public boolean update(Task task) throws Exception {

        String sql = """
            UPDATE tasks
            SET title = ?, description = ?, status = ?, due_date = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND user_id = ?
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus());
            statement.setString(4, task.getDueDate());
            statement.setInt(5, task.getId());
            statement.setInt(6, task.getUserId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id, int userId) throws Exception {

        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, userId);

            return statement.executeUpdate() == 1;
        }
    }

    public List<Task> searchByUserId(int userId, String keyword) throws Exception {

        List<Task> tasks = new ArrayList<>();

        String sql = """
            SELECT id, user_id, title, description, status, due_date
            FROM tasks
            WHERE user_id = ?
              AND (title LIKE ? OR description LIKE ?)
            ORDER BY id DESC
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";
            statement.setInt(1, userId);
            statement.setString(2, searchKeyword);
            statement.setString(3, searchKeyword);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    tasks.add(new Task(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("due_date")
                    ));
                }
            }
        }

        return tasks;
    }


    public List<Task> findAllByUserIdSorted(int userId, String sort) throws Exception {

        String orderBy;

        switch (sort) {
            case "due_asc":
                orderBy = "due_date ASC, id DESC";
                break;
            case "due_desc":
                orderBy = "due_date DESC, id DESC";
                break;
            default:
                orderBy = "id DESC";
                break;
        }

        List<Task> tasks = new ArrayList<>();

        String sql = """
            SELECT id, user_id, title, description, status, due_date
            FROM tasks
            WHERE user_id = ?
            ORDER BY 
        """ + orderBy;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    tasks.add(new Task(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("due_date")
                    ));
                }
            }
        }

        return tasks;
    }

    public List<Task> searchByUserIdSorted(int userId, String keyword, String sort) throws Exception {

        String orderBy;

        switch (sort) {
            case "due_asc":
                orderBy = "due_date ASC, id DESC";
                break;
            case "due_desc":
                orderBy = "due_date DESC, id DESC";
                break;
            default:
                orderBy = "id DESC";
                break;
        }

        List<Task> tasks = new ArrayList<>();

        String sql = """
            SELECT id, user_id, title, description, status, due_date
            FROM tasks
            WHERE user_id = ?
              AND (title LIKE ? OR description LIKE ?)
            ORDER BY 
        """ + orderBy;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";
            statement.setInt(1, userId);
            statement.setString(2, searchKeyword);
            statement.setString(3, searchKeyword);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    tasks.add(new Task(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("due_date")
                    ));
                }
            }
        }

        return tasks;
    }



    // ページネーション：ユーザー別タスク一覧
    public List<Task> findAllByUserIdSortedPaged(
            int userId, String sort, int limit, int offset) throws Exception {

        String orderBy;

        switch (sort) {
            case "due_asc":
                orderBy = "due_date ASC, id DESC";
                break;
            case "due_desc":
                orderBy = "due_date DESC, id DESC";
                break;
            default:
                orderBy = "id DESC";
                break;
        }

        List<Task> tasks = new ArrayList<>();

        String sql = """
            SELECT id, user_id, title, description, status, due_date
            FROM tasks
            WHERE user_id = ?
            ORDER BY
        """ + orderBy + " LIMIT ? OFFSET ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new Task(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("due_date")
                    ));
                }
            }
        }

        return tasks;
    }

    // ページネーション：検索結果
    public List<Task> searchByUserIdSortedPaged(
            int userId, String keyword, String sort, int limit, int offset) throws Exception {

        String orderBy;

        switch (sort) {
            case "due_asc":
                orderBy = "due_date ASC, id DESC";
                break;
            case "due_desc":
                orderBy = "due_date DESC, id DESC";
                break;
            default:
                orderBy = "id DESC";
                break;
        }

        List<Task> tasks = new ArrayList<>();

        String sql = """
            SELECT id, user_id, title, description, status, due_date
            FROM tasks
            WHERE user_id = ?
              AND (title LIKE ? OR description LIKE ?)
            ORDER BY
        """ + orderBy + " LIMIT ? OFFSET ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";
            statement.setInt(1, userId);
            statement.setString(2, searchKeyword);
            statement.setString(3, searchKeyword);
            statement.setInt(4, limit);
            statement.setInt(5, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new Task(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("due_date")
                    ));
                }
            }
        }

        return tasks;
    }

    // ユーザー別タスク件数取得
    public int countByUserId(int userId) throws Exception {

        String sql = "SELECT COUNT(*) FROM tasks WHERE user_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    // ユーザー別検索結果件数取得
    public int countSearchByUserId(int userId, String keyword) throws Exception {

        String sql = """
            SELECT COUNT(*)
            FROM tasks
            WHERE user_id = ?
              AND (title LIKE ? OR description LIKE ?)
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";
            statement.setInt(1, userId);
            statement.setString(2, searchKeyword);
            statement.setString(3, searchKeyword);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

}
