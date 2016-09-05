package com.practicaSV.gameLabz.configurations;

import com.practicaSV.gameLabz.exceptions.AuthenticationException;
import com.practicaSV.gameLabz.services.AuthenticationService;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@Component
public class ChannelInterceptor extends ChannelInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            if (headerAccessor.getNativeHeader(PathConstants.USER_NAME_KEY) != null || !headerAccessor.getNativeHeader(PathConstants.USER_NAME_KEY).isEmpty()) {
                String userName = headerAccessor.getNativeHeader(PathConstants.USER_NAME_KEY).stream().findFirst().get();

                if (headerAccessor.getNativeHeader(HttpHeadersConstants.SESSION_ID) != null || !headerAccessor.getNativeHeader(HttpHeadersConstants.SESSION_ID).isEmpty()) {
                    String sessionId = headerAccessor.getNativeHeader(HttpHeadersConstants.SESSION_ID).stream().findFirst().get();

                    if (!authenticationService.validateSession(userName, sessionId)) {
                        logger.error("Failed to subscribe! Username " + userName + " session id " + sessionId + " don't match");
                        throw new AuthenticationException(HttpStatus.FORBIDDEN, "Username and session id don't match!");
                    }
                }

                UriTemplate uriTemplate = new UriTemplate("/topic/{" + PathConstants.USER_NAME_KEY + "}/{.*}");

                if (uriTemplate.matches(headerAccessor.getDestination())) {

                    Map<String, String> matcher = uriTemplate.match(headerAccessor.getDestination());

                    if (!matcher.get(PathConstants.USER_NAME_KEY).equals(userName)) {
                        logger.error("Username " + userName + " not allowed to subscribe!");
                        throw new AuthenticationException(HttpStatus.FORBIDDEN, "Username not allowed to subscribe!");
                    }
                } else {
                    logger.error(headerAccessor.getDestination() + " is a wrong destination!");
                    throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Wrong destination!");
                }

            } else {
                logger.error("Failed to subscribe! Username is null!");
                throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Failed to subscribe!");
            }
        }
        return super.preSend(message, channel);
    }
}
