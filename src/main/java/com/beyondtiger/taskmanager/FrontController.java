package com.beyondtiger.taskmanager;

import java.io.IOException;

import com.beyondtiger.taskmanager.database.Database;
import com.beyondtiger.taskmanager.model.User;
import com.beyondtiger.taskmanager.service.AuthService;
import com.beyondtiger.taskmanager.dao.TaskDAO;
import com.beyondtiger.taskmanager.model.Task;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FrontController extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        Database.initialize();
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/controller/login");
            return;
        }

        switch (path) {
            case "/login":
                forward(request, response, "/WEB-INF/views/auth/login.jsp");
                break;

            case "/register":
                forward(request, response, "/WEB-INF/views/auth/register.jsp");
                break;

            case "/logout":
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/controller/login");
                break;

            case "/tasks/create":
                if (!isLoggedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/controller/login");
                    return;
                }
                forward(request, response, "/WEB-INF/views/tasks/create.jsp");
                break;

            case "/tasks/edit":
                if (!isLoggedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/controller/login");
                    return;
                }
                try {
                    User editUser = (User) request.getSession(false).getAttribute("user");
                    int editId = Integer.parseInt(request.getParameter("id"));
                    TaskDAO editDAO = new TaskDAO();
                    Task editTask = editDAO.findByIdAndUserId(editId, editUser.getId());

                    if (editTask == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }

                    request.setAttribute("task", editTask);
                    forward(request, response, "/WEB-INF/views/tasks/edit.jsp");
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                } catch (Exception e) {
                    throw new ServletException(e);
                }
                break;

            case "/tasks":
                if (!isLoggedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/controller/login");
                    return;
                }
                try {
                    User loggedInUser = (User) request.getSession(false).getAttribute("user");
                    TaskDAO taskDAO = new TaskDAO();
                    String keyword = request.getParameter("keyword");
                    String sort = request.getParameter("sort");
                    String pageParam = request.getParameter("page");

                    if (keyword == null) keyword = "";
                    keyword = keyword.trim();

                    if (!"due_asc".equals(sort) && !"due_desc".equals(sort)) {
                        sort = "default";
                    }

                    int page = 1;
                    try {
                        if (pageParam != null) {
                            page = Integer.parseInt(pageParam);
                            if (page < 1) page = 1;
                        }
                    } catch (NumberFormatException e) {
                        page = 1;
                    }

                    int pageSize = 5;
                    int totalCount;

                    if (keyword.isEmpty()) {
                        totalCount = taskDAO.countByUserId(loggedInUser.getId());
                    } else {
                        totalCount = taskDAO.countSearchByUserId(loggedInUser.getId(), keyword);
                    }

                    int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / pageSize));
                    if (page > totalPages) page = totalPages;

                    int offset = (page - 1) * pageSize;
                    List<Task> tasks;

                    if (keyword.isEmpty()) {
                        tasks = taskDAO.findAllByUserIdSortedPaged(
                            loggedInUser.getId(), sort, pageSize, offset);
                    } else {
                        tasks = taskDAO.searchByUserIdSortedPaged(
                            loggedInUser.getId(), keyword, sort, pageSize, offset);
                    }

                    request.setAttribute("tasks", tasks);
                    request.setAttribute("keyword", keyword);
                    request.setAttribute("sort", sort);
                    request.setAttribute("currentPage", page);
                    request.setAttribute("totalPages", totalPages);
                    forward(request, response, "/WEB-INF/views/tasks/list.jsp");
                } catch (Exception e) {
                    throw new ServletException(e);
                }
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();

        if (path == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (path) {
            case "/login":
                handleLogin(request, response);
                break;

            case "/register":
                handleRegister(request, response);
                break;

            case "/tasks/edit":
                handleTaskEdit(request, response);
                break;

            case "/tasks/delete":
                handleTaskDelete(request, response);
                break;
            case "/tasks/copy":
                handleTaskCopy(request, response);
                break;

            case "/tasks/create":
                handleTaskCreate(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = authService.login(username, password);

            if (user == null) {
                request.setAttribute("error", "ユーザー名またはパスワードが正しくありません。");
                forward(request, response, "/WEB-INF/views/auth/login.jsp");
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("user", new User(user.getId(), user.getUsername(), null));

            response.sendRedirect(request.getContextPath() + "/controller/tasks");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().length() < 3
                || username.trim().length() > 50) {

            request.setAttribute("error", "ユーザー名は3文字以上50文字以内で入力してください。");
            forward(request, response, "/WEB-INF/views/auth/register.jsp");
            return;
        }

        if (password == null || password.length() < 8) {
            request.setAttribute("error", "パスワードは8文字以上で入力してください。");
            forward(request, response, "/WEB-INF/views/auth/register.jsp");
            return;
        }

        try {
            boolean registered = authService.register(username, password);

            if (!registered) {
                request.setAttribute("error", "そのユーザー名は使用できません。");
                forward(request, response, "/WEB-INF/views/auth/register.jsp");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/controller/login");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleTaskCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/controller/login");
            return;
        }

        User loggedInUser = (User) request.getSession(false).getAttribute("user");

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String status = request.getParameter("status");
        String dueDate = request.getParameter("dueDate");

        if (title == null || title.trim().isEmpty() || title.trim().length() > 100) {
            request.setAttribute("error", "タイトルは1文字以上100文字以内で入力してください。");
            forward(request, response, "/WEB-INF/views/tasks/create.jsp");
            return;
        }

        if (description != null && description.length() > 1000) {
            request.setAttribute("error", "内容は1000文字以内で入力してください。");
            forward(request, response, "/WEB-INF/views/tasks/create.jsp");
            return;
        }

        if (!"未着手".equals(status) && !"進行中".equals(status) && !"完了".equals(status)) {
            request.setAttribute("error", "ステータスが不正です。");
            forward(request, response, "/WEB-INF/views/tasks/create.jsp");
            return;
        }

        if (dueDate != null && !dueDate.isEmpty()) {
            try {
                java.time.LocalDate.parse(dueDate);
            } catch (java.time.format.DateTimeParseException e) {
                request.setAttribute("error", "期限の日付が不正です。");
                forward(request, response, "/WEB-INF/views/tasks/create.jsp");
                return;
            }
        }

        try {
            Task task = new Task();
            task.setUserId(loggedInUser.getId());
            task.setTitle(title.trim());
            task.setDescription(description == null ? "" : description.trim());
            task.setStatus(status);
            task.setDueDate(dueDate == null || dueDate.isEmpty() ? null : dueDate);

            TaskDAO taskDAO = new TaskDAO();
            taskDAO.create(task);

            response.sendRedirect(request.getContextPath() + "/controller/tasks");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleTaskEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/controller/login");
            return;
        }

        User loggedInUser = (User) request.getSession(false).getAttribute("user");

        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String status = request.getParameter("status");
        String dueDate = request.getParameter("dueDate");

        int id;

        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            TaskDAO taskDAO = new TaskDAO();
            Task existingTask = taskDAO.findByIdAndUserId(id, loggedInUser.getId());

            if (existingTask == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (title == null || title.trim().isEmpty() || title.trim().length() > 100) {
                request.setAttribute("error", "タイトルは1文字以上100文字以内で入力してください。");
                request.setAttribute("task", existingTask);
                forward(request, response, "/WEB-INF/views/tasks/edit.jsp");
                return;
            }

            if (description != null && description.length() > 1000) {
                request.setAttribute("error", "内容は1000文字以内で入力してください。");
                request.setAttribute("task", existingTask);
                forward(request, response, "/WEB-INF/views/tasks/edit.jsp");
                return;
            }

            if (!"未着手".equals(status) && !"進行中".equals(status) && !"完了".equals(status)) {
                request.setAttribute("error", "ステータスが不正です。");
                request.setAttribute("task", existingTask);
                forward(request, response, "/WEB-INF/views/tasks/edit.jsp");
                return;
            }

            if (dueDate != null && !dueDate.isEmpty()) {
                try {
                    java.time.LocalDate.parse(dueDate);
                } catch (java.time.format.DateTimeParseException e) {
                    request.setAttribute("error", "期限の日付が不正です。");
                    request.setAttribute("task", existingTask);
                    forward(request, response, "/WEB-INF/views/tasks/edit.jsp");
                    return;
                }
            }

            existingTask.setTitle(title.trim());
            existingTask.setDescription(description == null ? "" : description.trim());
            existingTask.setStatus(status);
            existingTask.setDueDate(dueDate == null || dueDate.isEmpty() ? null : dueDate);

            taskDAO.update(existingTask);

            response.sendRedirect(request.getContextPath() + "/controller/tasks");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleTaskDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/controller/login");
            return;
        }

        User loggedInUser = (User) request.getSession().getAttribute("user");
        String idParam = request.getParameter("id");

        int id;

        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            TaskDAO taskDAO = new TaskDAO();

            Task existingTask = taskDAO.findByIdAndUserId(id, loggedInUser.getId());

            if (existingTask == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            taskDAO.delete(id, loggedInUser.getId());

            response.sendRedirect(request.getContextPath() + "/controller/tasks");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleTaskCopy(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/controller/login");
            return;
        }

        User loggedInUser = (User) request.getSession(false).getAttribute("user");

        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            TaskDAO taskDAO = new TaskDAO();
            Task original = taskDAO.findByIdAndUserId(id, loggedInUser.getId());

            if (original == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String copiedTitle = original.getTitle() + " (コピー)";
            if (copiedTitle.length() > 100) {
                copiedTitle = original.getTitle().substring(0, 100 - " (コピー)".length()) + " (コピー)";
            }

            Task copied = new Task();
            copied.setUserId(loggedInUser.getId());
            copied.setTitle(copiedTitle);
            copied.setDescription(original.getDescription());
            copied.setStatus(original.getStatus());
            copied.setDueDate(original.getDueDate());

            taskDAO.create(copied);

            response.sendRedirect(request.getContextPath() + "/controller/tasks");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response,
                        String path) throws ServletException, IOException {

        request.getRequestDispatcher(path).forward(request, response);
    }
}
