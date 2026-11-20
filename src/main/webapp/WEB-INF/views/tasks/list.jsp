<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.beyondtiger.taskmanager.model.Task" %>
<%@ page import="com.beyondtiger.taskmanager.model.User" %>

<%
    User user = (User) session.getAttribute("user");
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) keyword = "";
    String sort = (String) request.getAttribute("sort");
    if (sort == null) sort = "default";
    int currentPage = (Integer) request.getAttribute("currentPage");
    int totalPages = (Integer) request.getAttribute("totalPages");
    String encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>タスク一覧 | BEYOND TIGER HD TASKMANAGER</title>
</head>
<body>

    <header>
        <h1>BEYOND TIGER HD TASKMANAGER</h1>
        <p>ようこそ、<%= user.getUsername() %> さん</p>
        <a href="<%= request.getContextPath() %>/controller/logout">ログアウト</a>
    </header>

    <main>
        <h2>タスク一覧</h2>

        <form method="get" action="<%= request.getContextPath() %>/controller/tasks">
            <input type="text" name="keyword" value="<%= keyword %>" placeholder="タイトル・内容で検索">
            <button type="submit">検索</button>
            <a href="<%= request.getContextPath() %>/controller/tasks">検索解除</a>
        </form>

        <form method="get" action="<%= request.getContextPath() %>/controller/tasks">
            <input type="hidden" name="keyword" value="<%= keyword %>">
            <label for="sort">並び替え：</label>
            <select name="sort" id="sort" onchange="this.form.submit()">
                <option value="default" <%= "default".equals(sort) ? "selected" : "" %>>登録順</option>
                <option value="due_asc" <%= "due_asc".equals(sort) ? "selected" : "" %>>期限が近い順</option>
                <option value="due_desc" <%= "due_desc".equals(sort) ? "selected" : "" %>>期限が遠い順</option>
            </select>
        </form>

        <p>
            <a href="<%= request.getContextPath() %>/controller/tasks/create">
                タスクを新規登録する
            </a>
        </p>

        <% if (tasks == null || tasks.isEmpty()) { %>
            <% if (!keyword.isEmpty()) { %>
                <p>「<%= keyword %>」に一致するタスクはありません。</p>
            <% } else { %>
                <p>現在、登録されているタスクはありません。</p>
            <% } %>
        <% } else { %>

            <table border="1">
                <thead>
                    <tr>
                        <th>タイトル</th>
                        <th>内容</th>
                        <th>ステータス</th>
                        <th>期限</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Task task : tasks) { %>
                        <tr>
                            <td><%= task.getTitle() %></td>
                            <td><%= task.getDescription() == null ? "" : task.getDescription() %></td>
                            <td><%= task.getStatus() %></td>
                            <td><%= task.getDueDate() == null ? "" : task.getDueDate() %></td>
                            <td>
                                <a href="<%= request.getContextPath() %>/controller/tasks/edit?id=<%= task.getId() %>">編集</a>
                                <form method="post" action="<%= request.getContextPath() %>/controller/tasks/copy" style="display:inline;">
                                    <input type="hidden" name="id" value="<%= task.getId() %>">
                                    <button type="submit">コピー</button>
                                </form>
                                <form method="post" action="<%= request.getContextPath() %>/controller/tasks/delete" style="display:inline;">
                                    <input type="hidden" name="id" value="<%= task.getId() %>">
                                    <button type="submit" onclick="return confirm('このタスクを削除しますか？');">削除</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>

            <% if (totalPages > 1) { %>
                <nav aria-label="ページ切り替え">
                    <p>ページ <%= currentPage %> / <%= totalPages %></p>

                    <% if (currentPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/controller/tasks?page=<%= currentPage - 1 %>&keyword=<%= encodedKeyword %>&sort=<%= sort %>">前へ</a>
                    <% } %>

                    <% for (int p = 1; p <= totalPages; p++) { %>
                        <% if (p == currentPage) { %>
                            <strong><%= p %></strong>
                        <% } else { %>
                            <a href="<%= request.getContextPath() %>/controller/tasks?page=<%= p %>&keyword=<%= encodedKeyword %>&sort=<%= sort %>"><%= p %></a>
                        <% } %>
                    <% } %>

                    <% if (currentPage < totalPages) { %>
                        <a href="<%= request.getContextPath() %>/controller/tasks?page=<%= currentPage + 1 %>&keyword=<%= encodedKeyword %>&sort=<%= sort %>">次へ</a>
                    <% } %>
                </nav>
            <% } %>

        <% } %>
    </main>

</body>
</html>
