<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>新規登録 | BEYOND TIGER HD TASKMANAGER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/rainbow.css">
</head>
<body>
    <h1>BEYOND TIGER HD TASKMANAGER</h1>
    <h2>新規ユーザー登録</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="${pageContext.request.contextPath}/controller/register" method="post">
        <div>
            <label for="username">ユーザー名</label>
            <input type="text" id="username" name="username" maxlength="50" required>
        </div>

        <div>
            <label for="password">パスワード</label>
            <input type="password" id="password" name="password" minlength="8" required>
        </div>

        <button type="submit">登録</button>
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/controller/login">
            ログインはこちら
        </a>
    </p>
</body>
</html>
