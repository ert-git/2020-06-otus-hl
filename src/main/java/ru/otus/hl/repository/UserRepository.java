package ru.otus.hl.repository;

import java.util.List;
import java.util.Optional;

import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.User;

public interface UserRepository {
    Optional<User> findById(long id);
    Optional<User> findByLogin(String login);

    long insert(User user);

    void update(User user);

    List<User> getAll();

    SessionManager getSessionManager();
    void addFriend(User user, long friendId);
    void removeFriend(User user, long friendId);
    List<User> getFriends(User user);
}
