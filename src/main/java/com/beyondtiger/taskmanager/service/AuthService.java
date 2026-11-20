package com.beyondtiger.taskmanager.service;

import com.beyondtiger.taskmanager.dao.UserDAO;
import com.beyondtiger.taskmanager.model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public boolean register(String username, String password) throws Exception {

        if (username == null || password == null) {
            return false;
        }

        username = username.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (userDAO.findByUsername(username) != null) {
            return false;
        }

        return userDAO.create(new User(username, password));
    }

    public User login(String username, String password) throws Exception {

        if (username == null || password == null) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());

        if (user == null) {
            return null;
        }

        if (PasswordUtil.verify(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}
