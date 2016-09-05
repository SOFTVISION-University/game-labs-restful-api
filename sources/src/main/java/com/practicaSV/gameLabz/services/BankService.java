package com.practicaSV.gameLabz.services;

import com.practicaSV.gameLabz.domain.AMQPResponse;
import com.practicaSV.gameLabz.domain.TransactionInformation;

public interface BankService {

    AMQPResponse handle(TransactionInformation information);
}
