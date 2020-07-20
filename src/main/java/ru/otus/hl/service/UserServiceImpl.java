package ru.otus.hl.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.City;
import ru.otus.hl.model.Interest;
import ru.otus.hl.model.User;
import ru.otus.hl.repository.CityRepository;
import ru.otus.hl.repository.InterestRepository;
import ru.otus.hl.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userDao;
    private final CityRepository cityDao;

    private final BCryptPasswordEncoder encoder;

    private final InterestRepository interestDao;

    public UserServiceImpl(UserRepository userDao, CityRepository cityDao, InterestRepository interestDao, BCryptPasswordEncoder encoder) {
        this.userDao = userDao;
        this.cityDao = cityDao;
        this.interestDao = interestDao;
        this.encoder = encoder;
    }

    @Override
    public long create(User user) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                if (user.hasCity()) {
                    Optional<City> city = cityDao.findByName(user.getCity().getName());
                    if (!city.isPresent()) {
                        user.getCity().setId(cityDao.insert(user.getCity()));
                    } else {
                        user.getCity().setId(city.get().getId());
                    }
                }
                user.setPassword(encoder.encode(user.getPassword()));
                long userId = userDao.insert(user);
                sessionManager.commitSession();

                logger.info("created user: {}", userId);
                return userId;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public Optional<User> findById(long id) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                Optional<User> userOptional = userDao.findById(id);

                logger.info("user: {}", userOptional.orElse(null));
                return userOptional;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                return userDao.findByLogin(login);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return Optional.empty();
        }
    }

    @Override
    public void setInterests(User user) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            user.setInterests(String.join(", ", interestDao.getForUser(user)
                    .stream()
                    .map(i -> i.getName())
                    .collect(Collectors.toList())));
            sessionManager.commitSession();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void update(User user) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                if (user.hasCity()) {
                    Optional<City> city = cityDao.findByName(user.getCity().getName());
                    if (!city.isPresent()) {
                        user.getCity().setId(cityDao.insert(user.getCity()));
                    } else {
                        user.getCity().setId(city.get().getId());
                    }
                }
                userDao.update(user);
                if (user.getInterests() != null && !user.getInterests().isEmpty()) {
                    Arrays.stream(user.getInterests().split(",")).forEach(name -> {
                        Optional<Interest> interest = interestDao.findByName(user, name.trim());
                        if (!interest.isPresent()) {
                            interestDao.insert(user, new Interest(name));
                        } else {
                            interestDao.insert(user, interest.get());
                        }
                    });
                }
                sessionManager.commitSession();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public List<User> getAll() {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                return userDao.getAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return Collections.emptyList();
        }
    }

    @Override
    public void addFriend(User user, long friendId) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                userDao.addFriend(user, friendId);
                sessionManager.commitSession();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public void removeFriend(User user, long friendId) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                userDao.removeFriend(user, friendId);
                sessionManager.commitSession();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public List<User> getFriends(User user) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                return userDao.getFriends(user);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return Collections.emptyList();
        }
    }
}
