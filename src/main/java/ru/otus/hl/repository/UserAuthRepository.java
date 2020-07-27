package ru.otus.hl.repository;

import java.util.Optional;

import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.UserAuth;

public interface UserAuthRepository {

    Optional<UserAuth> findByLogin(String login);

    SessionManager getSessionManager();
}
