# BEYOND TIGER HD TASKMANAGER

## 概要
Javaを使用して制作したWebアプリケーションです。
ユーザーごとにタスクを管理できるタスク管理アプリケーションです。

## 主な機能
- ユーザー新規登録
- ログイン・ログアウト
- セッションによるログイン状態管理
- タスクの新規作成
- タスク一覧表示
- タスク編集
- タスク削除
- タスク検索
- 期限によるソート（昇順・降順）
- ページネーション
- 入力バリデーション

## 使用技術
- Java 21
- Jakarta Servlet
- JSP
- Maven
- Apache Tomcat 10.1
- SQLite

## 構成
- FrontController：リクエスト処理・画面遷移
- DAO：データベース操作
- Model：ユーザー・タスクデータ
- Service：認証・パスワード関連処理
- JSP：Web画面

## 画面
- ログイン
- 新規登録
- タスク一覧
- タスク作成
- タスク編集

## 動作環境
- Java 21
- Jakarta Servlet 6.0
- Apache Tomcat 10.1.62
- Maven 3.10.1
- SQLite
- SQLite JDBC 3.50.3.0

## ビルド方法
プロジェクトのルートディレクトリで以下を実行します。

mvn package

## 成果物
target/taskmanager.war

## 起動方法
1. MavenでWARファイルを生成します。
2. Apache Tomcatのwebappsディレクトリにtaskmanager.warを配置します。
3. Tomcatを起動します。
4. ブラウザで以下にアクセスします。

http://localhost:8080/taskmanager/

