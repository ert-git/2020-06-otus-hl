package ru.otus.hl.db.sessionmanager;

import java.sql.Connection;

public class DatabaseSessionJdbc implements DatabaseSession {
    private final Connection connection;

    DatabaseSessionJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
