package ru.otus.hl.repository;

import java.util.List;
import java.util.Optional;

import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.City;

public interface CityRepository {
    Optional<City> findById(long id);

    int insert(City city);

    void update(City city);

    Optional<City> findByName(String name);

    List<City> getAll();

    SessionManager getSessionManager();
}
