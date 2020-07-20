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
import ru.otus.hl.model.Interest;
import ru.otus.hl.model.User;

@Repository
public class InterestRepositoryImpl implements InterestRepository {
    private static final Logger logger = LoggerFactory.getLogger(InterestRepositoryImpl.class);

    private final SessionManager sessionManager;

    private static final String INSERT_SQL = "INSERT INTO interests (name) VALUES (?)";
    private static final String INSERT_FOR_USER = "INSERT INTO user_interest select ?, ?";
    private static final String SELECT_BY_ID_SQL = "SELECT id, name FROM interests WHERE id =?";
    private static final String SELECT_FOR_USER = "SELECT i.id as id, name FROM interests i "
            + " LEFT JOIN user_interest ui ON i.id = ui.interest_id "
            + " WHERE ui.user_id = ?";
    private static final String SELECT_BY_NAME = "SELECT i.id as id, name, ui.user_id as userId FROM interests i "
            + " LEFT JOIN user_interest ui ON (i.id = ui.interest_id AND ui.user_id=?) "
            + " WHERE i.name = ?";

    public InterestRepositoryImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<Interest> findById(long id) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Interest(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public void insert(User user, Interest interest) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        if (interest.getId() == 0) {
            try (PreparedStatement pst = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, interest.getName());
                pst.executeUpdate();
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    rs.next();
                    interest.setId(rs.getLong(1));
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
                return;
            }
        }
        try (PreparedStatement pst = connection.prepareStatement(INSERT_FOR_USER)) {
            pst.setLong(1, user.getId());
            pst.setLong(2, interest.getId());
            pst.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Interest> findByName(User user, String name) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_NAME)) {
            pst.setLong(1, user.getId());
            pst.setString(2, name);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Interest interest = new Interest(rs.getLong("id"), rs.getString("name"));
                    Object userId = rs.getObject("userId");
                    if (userId != null) {
                        interest.setUsers(new ArrayList<>());
                        interest.getUsers().add(new User((Integer) userId));
                    }
                    return Optional.of(interest);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Interest> getForUser(User user) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_FOR_USER)) {
            pst.setLong(1, user.getId());
            try (ResultSet rs = pst.executeQuery()) {
                List<Interest> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Interest(rs.getInt(1), rs.getString(2)));
                }
                return list;
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Interest> getAll() {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            try (ResultSet rs = pst.executeQuery()) {
                List<Interest> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Interest(rs.getLong(1), rs.getString(2)));
                }
                return list;
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
