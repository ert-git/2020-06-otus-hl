package ru.otus.hl.db.sessionmanager;

import java.sql.Connection;

public interface DatabaseSession {
    Connection getConnection();
}
