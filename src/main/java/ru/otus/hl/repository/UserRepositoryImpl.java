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
import ru.otus.hl.model.Gender;
import ru.otus.hl.model.User;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final SessionManager sessionManager;

    private static final String UPDATE_SQL = "UPDATE users SET first_name=?, last_name=?, second_name=?, age=?, gender=?, city=?"
            + " WHERE id=?";
    private static final String INSERT_SQL = "INSERT INTO users (first_name, last_name, second_name, age, gender, city, login, password)"
            + "VALUES (?,?,?,?,?,?,?,?)";
    private static final String SELECT_USER_SQL = "SELECT u.id id, login, first_name, last_name, second_name, age, gender, city cityId, c.name cityName"
            + " FROM users u "
            + " LEFT JOIN cities c ON u.city = c.id";
    private static final String SELECT_BY_ID_SQL = SELECT_USER_SQL + " WHERE u.id =?";
    private static final String SELECT_BY_LOGIN_SQL = SELECT_USER_SQL + " WHERE login =?";
    private static final String ADD_FRIEND_SQL = "INSERT INTO user_friend (user_id, friend_id) VALUES (?,?)";
    private static final String DEL_FRIEND_SQL = "DELETE FROM user_friend WHERE user_id=? AND friend_id = ?";
    private static final String SELECT_FRIENDS_SQL = SELECT_USER_SQL 
            + " LEFT JOIN user_friend uf ON u.id = uf.friend_id WHERE uf.user_id = ?";

    public UserRepositoryImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<User> findById(long id) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }
    @Override
    public Optional<User> findByLogin(String login) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_BY_LOGIN_SQL)) {
            pst.setString(1, login);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public long insert(User user) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, user.getSecondName());
            pst.setObject(4, user.getAge());
            pst.setInt(5, user.getGender().getValue());
            if (user.getCity() != null) {
                pst.setInt(6, user.getCity().getId());
            } else {
                pst.setObject(6, null);
            }
            pst.setString(7, user.getLogin());
            pst.setString(8, user.getPassword());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                user.setId(rs.getLong(1));
            }
            return user.getId();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return 0;
    }

    @Override
    public void update(User user) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(UPDATE_SQL)) {
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, user.getSecondName());
            pst.setObject(4, user.getAge());
            pst.setInt(5, user.getGender().getValue());
            if (user.getCity() != null) {
                pst.setInt(6, user.getCity().getId());
            } else {
                pst.setObject(6, null);
            }
            pst.setLong(7, user.getId());
            pst.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public List<User> getAll() {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_USER_SQL)) {
            try (ResultSet rs = pst.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
                return users;
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User(rs.getLong("id"), rs.getString("login"), null,
                rs.getString("first_name"), rs.getString("last_name"), rs.getString("second_name"),
                (Integer) rs.getObject("age"),
                Gender.fromValue((Integer) rs.getObject("gender")), null, null);
        if (rs.getObject("cityId") != null) {
            user.setCity(new City(rs.getInt("cityId"), rs.getString("cityName")));
        }
        return user;
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void addFriend(User user, long friendId) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        logger.info("addFriend: {}, user_id={}, friendId={}", ADD_FRIEND_SQL, user.getId(), friendId);
        try (PreparedStatement pst = connection.prepareStatement(ADD_FRIEND_SQL)) {
            pst.setLong(1, user.getId());
            pst.setLong(2, friendId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    @Override
    public void removeFriend(User user, long friendId) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        logger.info("removeFriend: {}, user_id={}, friendId={}", DEL_FRIEND_SQL, user.getId(), friendId);
        try (PreparedStatement pst = connection.prepareStatement(DEL_FRIEND_SQL)) {
            pst.setLong(1, user.getId());
            pst.setLong(2, friendId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    @Override
    public List<User> getFriends(User user) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        logger.info("getFriends: {}, id={}", SELECT_FRIENDS_SQL, user.getId());
        try (PreparedStatement pst = connection.prepareStatement(SELECT_FRIENDS_SQL)) {
            pst.setLong(1, user.getId());
            try (ResultSet rs = pst.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
                return users;
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }
}
