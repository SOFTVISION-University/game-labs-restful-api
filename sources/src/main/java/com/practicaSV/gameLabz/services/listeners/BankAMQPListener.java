package com.practicaSV.gameLabz.services.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaSV.gameLabz.domain.TransactionInformation;
import com.practicaSV.gameLabz.services.BankService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class BankAMQPListener {

    @Autowired
    private BankService bankService;

    public String listen(String data) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            TransactionInformation information = mapper.readValue(data, TransactionInformation.class);
            String response = mapper.writeValueAsString(bankService.handle(information));
            return response;
        } catch (IOException e) {
            return null;
        }
    }
}
