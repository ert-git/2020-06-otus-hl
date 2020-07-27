package ru.otus.hl.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.otus.hl.db.sessionmanager.SessionManager;
import ru.otus.hl.model.UserAuth;
import ru.otus.hl.model.UserDetailsImpl;
import ru.otus.hl.repository.UserAuthRepository;

@Slf4j
@Service
public class UserAuthServiceImpl implements UserDetailsService {

    private final UserAuthRepository userDao;

    public UserAuthServiceImpl(UserAuthRepository userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: username={}", username);
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            Optional<UserAuth> auth = userDao.findByLogin(username);
            if (!auth.isPresent()) {
                log.error("loadUserByUsername: username={} not found", username);
                throw new UsernameNotFoundException(username);
            }
            return new UserDetailsImpl(auth.get());
        }
    }

}
