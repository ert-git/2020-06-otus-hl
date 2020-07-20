package ru.otus.hl.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.UserAuth;

@Repository
public class UserAuthRepositoryImpl implements UserAuthRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthRepositoryImpl.class);

    private final SessionManager sessionManager;

    private static final String SELECT_ALL_SQL = "select id, login, password from users";
    private static final String SELECT_FOR_AUTH_SQL = SELECT_ALL_SQL + " where login =?";

    public UserAuthRepositoryImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public Optional<UserAuth> findByLogin(String login) {
        Connection connection = sessionManager.getCurrentSession().getConnection();
        try (PreparedStatement pst = connection.prepareStatement(SELECT_FOR_AUTH_SQL)) {
            pst.setString(1, login);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    UserAuth user = new UserAuth();
                    user.setId(rs.getLong("id"));
                    user.setLogin(rs.getString("login"));
                    user.setPassword(rs.getString("password"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

}
