package com.practicaSV.gameLabz.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaSV.gameLabz.domain.AMQPResponse;
import com.practicaSV.gameLabz.domain.TransactionInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

public class TransactionAMQPImpl implements Transaction {

    @Autowired
    private RabbitTemplate template;

//    @Value("${game.transaction.routing.key}")
    private String gameTransactionKey;

    @Override
    public AMQPResponse executeTransaction(TransactionInformation information) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(information);
            String response = (String) template.convertSendAndReceive(gameTransactionKey, json);
            return mapper.readValue(response, AMQPResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
