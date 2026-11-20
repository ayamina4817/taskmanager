<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.beyondtiger.taskmanager.model.Task" %>
<%@ page import="com.beyondtiger.taskmanager.model.User" %>

<%
    User user = (User) session.getAttribute("user");
    Task task = (Task) request.getAttribute("task");
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>タスク編集 | BEYOND TIGER HD TASKMANAGER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/rainbow.css">
</head>
<body>

    <header>
        <h1>BEYOND TIGER HD TASKMANAGER</h1>
        <p>ようこそ、<%= user.getUsername() %> さん</p>
    </header>

    <main>
        <h2>タスク編集</h2>

        <% if (error != null) { %>
            <p style="color:red;"><%= error %></p>
        <% } %>

        <form action="<%= request.getContextPath() %>/controller/tasks/edit" method="post">
            <input type="hidden" name="id" value="<%= task.getId() %>">

            <div>
                <label for="title">タイトル</label>
                <input type="text" id="title" name="title"
                       value="<%= task.getTitle() %>" maxlength="100" required>
            </div>

            <div>
                <label for="description">内容</label>
                <textarea id="description" name="description"
                          rows="5" maxlength="1000"><%= task.getDescription() == null ? "" : task.getDescription() %></textarea>
            </div>

            <div>
                <label for="status">ステータス</label>
                <select id="status" name="status" required>
                    <option value="未着手" <%= "未着手".equals(task.getStatus()) ? "selected" : "" %>>未着手</option>
                    <option value="進行中" <%= "進行中".equals(task.getStatus()) ? "selected" : "" %>>進行中</option>
                    <option value="完了" <%= "完了".equals(task.getStatus()) ? "selected" : "" %>>完了</option>
                </select>
            </div>

            <div>
                <label for="dueDate">期限</label>
                <input type="date" id="dueDate" name="dueDate"
                       value="<%= task.getDueDate() == null ? "" : task.getDueDate() %>">
            </div>

            <button type="submit">更新する</button>
        </form>

        <p>
            <a href="<%= request.getContextPath() %>/controller/tasks">
                タスク一覧に戻る
            </a>
        </p>
    </main>

</body>
</html>
