package com.moneycontrol.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DatabaseConnection {

    private static final String DB_DIR  = System.getProperty("user.home") + File.separator + "MoneyControl";
    private static final String DB_PATH = DB_DIR + File.separator + "moneycontrol.db";
    private static final String URL     = "jdbc:sqlite:" + DB_PATH;

    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            new File(DB_DIR).mkdirs();
            instance = DriverManager.getConnection(URL);
            instance.createStatement().execute("PRAGMA foreign_keys = ON");
            initSchema(instance);
        }
        return instance;
    }

    private static void initSchema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome       TEXT    NOT NULL,
                    email      TEXT    NOT NULL UNIQUE,
                    senha_hash TEXT    NOT NULL,
                    criado_em  TEXT    DEFAULT (date('now'))
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS gastos (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    descricao   TEXT    NOT NULL,
                    valor       REAL    NOT NULL,
                    data        TEXT    NOT NULL,
                    categoria   TEXT    NOT NULL,
                    usuario_id  INTEGER NOT NULL,
                    criado_em   TEXT    DEFAULT (datetime('now')),
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                )
            """);
        }
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
