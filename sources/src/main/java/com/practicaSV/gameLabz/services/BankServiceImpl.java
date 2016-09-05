package com.practicaSV.gameLabz.services;

import com.practicaSV.gameLabz.domain.AMQPResponse;
import com.practicaSV.gameLabz.domain.TransactionInformation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class BankServiceImpl implements BankService {

    @Override
    public AMQPResponse handle(TransactionInformation information) {

        AMQPResponse response = new AMQPResponse();

        StringBuilder errorMessage = new StringBuilder();
        boolean failed = false;

        if (information.getClientName() == null) {
            failed = true;
            errorMessage.append("Invalid client name! ");
        }

        if (information.getClientCardExpDate() == null) {
            failed = true;
            errorMessage.append("Client's card expiration date is invalid! ");
        }

        if (information.getClientCardExpDate() < System.currentTimeMillis()) {
            failed = true;
            errorMessage.append("Client's card has expired! ");
        }

        if (information.getClientCardNumber() == null) {
            failed = true;
            errorMessage.append("Client's card number is invalid! ");
        }

        if (information.getCvv() == null || information.getCvv() > 999 || information.getCvv() < 100) {
            failed = true;
            errorMessage.append("Invalid client cvv! ");
        }

        if (information.getGameLabzCardNumber() == null) {
            failed = true;
            errorMessage.append("Service's card number is invalid! ");
        }

        if (information.getOrderPrice().compareTo(BigDecimal.ZERO) == -1) {
            failed = true;
            errorMessage.append("Invalid price! ");
        }

        if (failed) {
            response.setStatus(AMQPResponse.ResponseStatus.ERROR);
            response.setMessage(errorMessage.toString().trim());
        } else {
            response.setStatus(AMQPResponse.ResponseStatus.OK);
        }

        return response;
    }
}
