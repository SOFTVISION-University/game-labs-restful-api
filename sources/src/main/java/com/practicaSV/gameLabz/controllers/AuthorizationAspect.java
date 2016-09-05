package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.exceptions.AuthenticationException;
import com.practicaSV.gameLabz.exceptions.AuthorizationException;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.utils.Authorization;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private UserDAO userDAO;

    @Before("@annotation(authorization) && args(userName)")
    public void handleAuthorization(String userName, Authorization authorization){

        User user = userDAO.getUserByUserName(userName).orElseThrow(() -> new AuthenticationException(HttpStatus.UNAUTHORIZED, "Invalid user!"));
        User.UserType dbUserType = user.getUserType();

        boolean isUserTypeCorrect = Arrays.asList(authorization.userTypes()).stream()
                .anyMatch((s) -> s == dbUserType);

        if (!isUserTypeCorrect) {
            throw new AuthorizationException(HttpStatus.FORBIDDEN, "Not allowed to call that request!");
        }

    }
}
