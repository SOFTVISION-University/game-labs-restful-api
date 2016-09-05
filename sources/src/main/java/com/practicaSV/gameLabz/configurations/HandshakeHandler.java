package com.practicaSV.gameLabz.configurations;

import com.practicaSV.gameLabz.services.AuthenticationService;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.Optional;

@Component
public class HandshakeHandler extends DefaultHandshakeHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected boolean isValidOrigin(ServerHttpRequest request) {

        Optional<String> userName = request.getHeaders().get(PathConstants.USER_NAME_KEY).stream().findFirst();
        if (!userName.isPresent()) {
            logger.error("Failed to validate session id for user! User name empty!");
            return false;
        }

        Optional<String> sessionId = request.getHeaders().get(HttpHeadersConstants.SESSION_ID).stream().findFirst();
        if (!sessionId.isPresent()) {
            logger.error("Failed to validate session id for user! Session id empty!");
            return false;
        }

        if (!authenticationService.validateSession(userName.get(), sessionId.get())) {
            logger.error("Invalid session! Username "+userName.get()+" and session id "+sessionId.get()+" don't match!");
            return false;
        }

        return true;
    }


}
