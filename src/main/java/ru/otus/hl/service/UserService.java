package ru.otus.hl.service;

import java.util.List;
import java.util.Optional;

import ru.otus.hl.model.User;

public interface UserService {

    long create(User user);

    void update(User user);

    Optional<User> findById(long id);

    Optional<User> findByLogin(String login);

    List<User> getAll();

    List<User> getFriends(User user);

    void addFriend(User user, long friendId);

    void removeFriend(User user, long friendId);

    void setInterests(User user);
}
