package ru.otus.hl.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.City;

@Repository
public class CityRepositoryImpl implements CityRepository {
    private static final Logger logger = LoggerFactory.getLogger(CityRepositoryImpl.class);

    private final SessionManager sessionManager;

    private static final String UPDATE_SQL = "update cities set name=? where id=?";
    private static final String INSERT_SQL = "insert into cities (name) values (?)";
    private static final String SELECT_BY_ID_SQL = "select id, name from cities where id =?";
    private static final String SELECT_BY_NAME = "select id, name from cities where name =?";

    public CityRepositoryImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<City> findById(long id) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new City(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public int insert(City city) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, city.getName());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                city.setId(rs.getInt(1));
            }
            return city.getId();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    @Override
    public void update(City city) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(UPDATE_SQL)) {
            pst.setString(1, city.getName());
            pst.setInt(2, city.getId());
            pst.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<City> findByName(String name) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_NAME)) {
            pst.setString(1, name);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new City(rs.getInt(1), name));
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<City> getAll() {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            try (ResultSet rs = pst.executeQuery()) {
                List<City> cities = new ArrayList<>();
                while (rs.next()) {
                    cities.add(new City(rs.getInt(1), rs.getString(2)));
                }
                return cities;
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

}
