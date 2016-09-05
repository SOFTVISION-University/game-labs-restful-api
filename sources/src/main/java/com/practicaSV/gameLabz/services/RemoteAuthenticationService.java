package com.practicaSV.gameLabz.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaSV.gameLabz.domain.AMQPResponse;
import com.practicaSV.gameLabz.exceptions.AuthenticationException;
import com.practicaSV.gameLabz.utils.AMQPListenerConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RemoteAuthenticationService implements AuthenticationService {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RabbitTemplate template;

//    @Value("${login.routing.key}")
    private String loginKey;

//    @Value("${session.validation.routing.key}")
    private String sessionValidationKey;

//    @Value("${logout.routing.key}")
    private String logoutKey;

    @Override
    public String doLogin(String userName, String pass) {

        Map<String, String> message = new HashMap<>();
        message.put(AMQPListenerConstants.USER_NAME, userName);
        message.put(AMQPListenerConstants.PASSWORD, pass);

        String response = (String) template.convertSendAndReceive(loginKey, message);
        try {
            AMQPResponse amqpResponse = mapper.readValue(response, AMQPResponse.class);
            if (amqpResponse.getStatus() == AMQPResponse.ResponseStatus.ERROR) {
                throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR, amqpResponse.getMessage());
            }
            return amqpResponse.getMessage();
        } catch (IOException e) {
            throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR, "Mapper error in login!");
        }
    }

    @Override
    public boolean validateSession(String userName, String sessionId) {

        Map<String, String> message = new HashMap<>();
        message.put(AMQPListenerConstants.USER_NAME, userName);
        message.put(AMQPListenerConstants.SESSION, sessionId);

        String response = (String) template.convertSendAndReceive(sessionValidationKey, message);
        try {
            AMQPResponse amqpResponse = mapper.readValue(response, AMQPResponse.class);
            if (amqpResponse.getStatus() == AMQPResponse.ResponseStatus.ERROR) {
                return false;
            }
            return true;
        } catch (IOException e) {
            throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR, "Mapper error in session validation!");
        }
    }

    @Override
    public void doLogout(String userName) {

        String response = (String) template.convertSendAndReceive(logoutKey, userName);
        try {
            AMQPResponse amqpResponse = mapper.readValue(response, AMQPResponse.class);
            if (amqpResponse.getStatus() == AMQPResponse.ResponseStatus.ERROR) {
                throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR, amqpResponse.getMessage());
            }
        } catch (IOException e) {
            throw new AuthenticationException(HttpStatus.INTERNAL_SERVER_ERROR, "Mapper error in logout!");
        }
    }
}
