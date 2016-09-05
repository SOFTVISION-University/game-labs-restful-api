package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.exceptions.AuthenticationException;
import com.practicaSV.gameLabz.services.AuthenticationService;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String sessionId = request.getHeader(HttpHeadersConstants.SESSION_ID);

        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String userName = pathVariables.get(PathConstants.USER_NAME_KEY).toString();

        if (StringUtils.isBlank(userName)) {
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "UserName was not found!");
        }

        if (StringUtils.isBlank(sessionId)) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Empty session id!");
        }

        if (!authenticationService.validateSession(userName, sessionId)) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Invalid session id!");
        }

        return true;
    }
}
