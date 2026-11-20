<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ログイン | BEYOND TIGER HD TASKMANAGER</title>
</head>
<body>
    <h1>BEYOND TIGER HD TASKMANAGER</h1>
    <h2>ログイン</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="${pageContext.request.contextPath}/controller/login" method="post">
        <div>
            <label for="username">ユーザー名</label>
            <input type="text" id="username" name="username" required>
        </div>

        <div>
            <label for="password">パスワード</label>
            <input type="password" id="password" name="password" required>
        </div>

        <button type="submit">ログイン</button>
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/controller/register">
            新規登録はこちら
        </a>
    </p>
</body>
</html>
