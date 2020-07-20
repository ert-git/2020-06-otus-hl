package ru.otus.hl.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.otus.hl.repository.UserAuthRepository;

@Slf4j
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    
    private final UserAuthRepository userDao;

    public AuthenticationSuccessHandlerImpl(UserAuthRepository userDao) {
        this.userDao = userDao;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest arg0, HttpServletResponse arg1, Authentication arg2) throws IOException, ServletException {
        //userDao.updateLastLogin(new Date());
        log.info("onAuthenticationSuccess: {}", arg2);
    }


}
