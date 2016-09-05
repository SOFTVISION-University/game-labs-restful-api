package com.practicaSV.gameLabz.services;

import com.practicaSV.gameLabz.domain.AMQPResponse;
import com.practicaSV.gameLabz.domain.TransactionInformation;

public interface Transaction {

    AMQPResponse executeTransaction(TransactionInformation information);
}
