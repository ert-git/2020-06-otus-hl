package ru.otus.hl.repository;

import java.util.List;
import java.util.Optional;

import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.Interest;
import ru.otus.hl.model.User;

public interface InterestRepository {
    Optional<Interest> findById(long id);

    void insert(User user, Interest interest);

    Optional<Interest> findByName(User user, String name);

    List<Interest> getAll();
    List<Interest> getForUser(User user);

    SessionManager getSessionManager();
}
