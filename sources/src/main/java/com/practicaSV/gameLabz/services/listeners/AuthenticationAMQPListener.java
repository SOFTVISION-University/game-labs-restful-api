package com.practicaSV.gameLabz.services.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaSV.gameLabz.domain.AMQPResponse;
import com.practicaSV.gameLabz.exceptions.AuthenticationException;
import com.practicaSV.gameLabz.services.AuthenticationService;
import com.practicaSV.gameLabz.utils.AMQPListenerConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class AuthenticationAMQPListener {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private AuthenticationService authenticationService;

    public String loginListen(Map data) throws JsonProcessingException {

        try {
            String userName = data.get(AMQPListenerConstants.USER_NAME).toString();
            String pass = data.get(AMQPListenerConstants.PASSWORD).toString();
            String sessionId = authenticationService.doLogin(userName, pass);
            AMQPResponse response = new AMQPResponse();
            response.setMessage(sessionId);
            response.setStatus(AMQPResponse.ResponseStatus.OK);

            return mapper.writeValueAsString(response);
        } catch (AuthenticationException e) {

            AMQPResponse response = new AMQPResponse();
            response.setStatus(AMQPResponse.ResponseStatus.ERROR);
            response.setMessage(e.getMessage());
            return mapper.writeValueAsString(response);
        }
    }

    public String validateSession(Map data) throws JsonProcessingException {

        AMQPResponse response = new AMQPResponse();
        String userName = data.get(AMQPListenerConstants.USER_NAME).toString();
        String sessionId = data.get(AMQPListenerConstants.SESSION).toString();
        if (authenticationService.validateSession(userName, sessionId)) {
            response.setStatus(AMQPResponse.ResponseStatus.OK);
        } else {
            response.setStatus(AMQPResponse.ResponseStatus.ERROR);
        }
        return mapper.writeValueAsString(response);
    }

    public String logoutListen(String userName) throws JsonProcessingException {

        AMQPResponse response = new AMQPResponse();
        try {
            authenticationService.doLogout(userName);
            response.setStatus(AMQPResponse.ResponseStatus.OK);
            return mapper.writeValueAsString(response);
        } catch (AuthenticationException e) {
            response.setStatus(AMQPResponse.ResponseStatus.ERROR);
            response.setMessage(e.getMessage());
            return mapper.writeValueAsString(response);
        }
    }
}
